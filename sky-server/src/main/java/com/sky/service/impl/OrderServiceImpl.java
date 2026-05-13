package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.service.ShoppingCartService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    @Transactional
    @Override
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {
        //1、处理各种业务异常
        AddressBook addressBook = addressBookMapper.getAddressBookById(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null) {
            //地址不存在
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        //查询当前用户购物车数据
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.getShoppingCartData(userId);
        if (shoppingCartList == null || shoppingCartList.isEmpty()) {
            //购物车为空
            throw new AddressBookBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        //2、添加订单数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        orders.setUserId(userId);
//        //查询用户名
//        orders.setUserName(userMapper.getById(userId).getName()); //用户名
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);//未支付
        orders.setStatus(Orders.PENDING_PAYMENT);//待付款 订单状态
        orders.setNumber(String.valueOf(System.currentTimeMillis()));//订单号
        orders.setPhone(addressBook.getPhone());//手机号
        orders.setConsignee(addressBook.getConsignee()); //收货人
        String address = addressBook.getProvinceName() + addressBook.getCityName() +
                addressBook.getDistrictName() + addressBook.getDetail();
        orders.setAddress(address);//地址
        orderMapper.insert(orders);
        List<OrderDetail> orderDetailList = new ArrayList<>();

        //3、添加订单明细数据
        for (ShoppingCart cart : shoppingCartList) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetailList.add(orderDetail);
        }
        orderDetailMapper.insertBatch(orderDetailList);

        //4、删除购物车数据
        shoppingCartMapper.deleteByUserId(userId);
        //5、返回OrderSubmitVO
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderNumber(orders.getNumber())
                .orderTime(orders.getOrderTime())
                .orderAmount(orders.getAmount())
                .build();
        return orderSubmitVO;
    }

    /**
     * 查询历史订单
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Long userId = BaseContext.getCurrentId();
        ordersPageQueryDTO.setUserId(userId);

        Page<Orders> page = orderMapper.pageQuery(ordersPageQueryDTO);

        List<OrderVO> orderVOList = new ArrayList<>();
        for (Orders orders : page.getResult()) {
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(orders, orderVO);
            // 查询订单明细
            List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(orders.getId());
            orderVO.setOrderDetailList(orderDetails);

            orderVOList.add(orderVO);
        }

        return new PageResult(page.getTotal(), orderVOList);
    }

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    @Override
    public OrderVO orderDetail(Long id) {
        //获取订单信息
        Orders orders = orderMapper.getById(id);

        //获取订单菜品信息
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);
        //将订单信息、订单菜品信息封装到orderVO中并返回
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        orderVO.setOrderDetailList(orderDetailList);
        return orderVO;
    }

    /**
     * 用户取消订单
     * @param id
     */
    @Override
    public void cancel(Long id, String reason) {
        //获取订单完整信息
        Orders orders = orderMapper.getById(id);
        //判断订单是否存在
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        //判断订单是否可取消(只有待付款和待接单才能取消)
        if (orders.getStatus() > Orders.TO_BE_CONFIRMED) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        orders.setStatus(Orders.CANCELLED);//订单状态：取消
        orders.setCancelReason(reason);//取消原因
        orders.setCancelTime(LocalDateTime.now());//取消时间

        orderMapper.update(orders);

    }

    /**
     * 再来一单
     * @param id
     */
    @Override
    @Transactional
    public void repetition(Long id) {
        //1、查询原订单信息
        Orders oldOrder = orderMapper.getById(id);
        if (oldOrder == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        //2、查询原订单明细
        List<OrderDetail> oldOrderDetails = orderDetailMapper.getByOrderId(id);
        if (oldOrderDetails == null || oldOrderDetails.isEmpty()) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        //3、将原订单明细添加到当前用户的购物车
        Long userId = BaseContext.getCurrentId();
        List<ShoppingCart> shoppingCartList = new ArrayList<>();
        for (OrderDetail orderDetail : oldOrderDetails) {
            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(orderDetail, shoppingCart);
            shoppingCart.setUserId(userId);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartList.add(shoppingCart);
        }

        //4、批量添加购物车数据
        shoppingCartMapper.insertBatch(shoppingCartList);
    }
    /**|
     * 订单支付
     */
    @Override
    public void payment(String orderNumber) {
        //1、查询订单
        Orders orders = orderMapper.getByNumber(orderNumber);
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        //2、修改订单状态
        orders.setStatus(Orders.TO_BE_CONFIRMED); //待接单
        //3、修改订单结账时间
        orders.setCheckoutTime(LocalDateTime.now());
        //4、修改订单支付状态
        orders.setPayStatus(Orders.PAID);

        //5、修改订单数据
        orderMapper.update(orders);

    }

    /**
     * 统计订单数据
     * @return
     */
    @Override
    public OrderStatisticsVO getStatisticsOrder() {
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        orderStatisticsVO.setToBeConfirmed(orderMapper.countStatus(Orders.TO_BE_CONFIRMED));//待接单
        orderStatisticsVO.setConfirmed(orderMapper.countStatus(Orders.CONFIRMED));//待派送
        orderStatisticsVO.setDeliveryInProgress(orderMapper.countStatus(Orders.DELIVERY_IN_PROGRESS));//待送餐

        return orderStatisticsVO;
    }

    /**
     * 订单搜索(商家)
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());

        Page<Orders> page = orderMapper.conditionSearch(ordersPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());

    }

    /**
     * 接单
     * @param ordersConfirmDTO
     */
    @Override
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        //1、查询订单是否存在
        Orders orders = orderMapper.getById(ordersConfirmDTO.getId());
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        //2、验证该订单状态
        if (orders.getStatus() != Orders.TO_BE_CONFIRMED) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        //3、修改订单状态
        orders.setStatus(Orders.CONFIRMED);
        orderMapper.update(orders);
    }

    /**
     * 拒单
     * @param ordersRejectionDTO
     */
    @Override
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) {
        //1、查询订单是否存在
        Orders orders = orderMapper.getById(ordersRejectionDTO.getId());
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        //2、验证该订单状态
        if (!Objects.equals(orders.getStatus(), Orders.TO_BE_CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        //3、如果订单为已支付，则需退款
        if (orders.getPayStatus() == Orders.PAID) {
            //todo 调用微信支付接口进行退款
            //先修改订单状态为退款中
            orders.setPayStatus(Orders.REFUND);
        }
        //4、修改订单状态为已取消
        orders.setStatus(Orders.CANCELLED);
        //5、记录拒绝原因和取消时间
        orders.setRejectionReason(ordersRejectionDTO.getRejectionReason());
        orders.setCancelTime(LocalDateTime.now());
        //6、修改订单数据
        orderMapper.update(orders);
    }

    /**
     * 取消订单
     * @param ordersCancelDTO
     */
    @Override
    public void cancelOrder(OrdersCancelDTO ordersCancelDTO) {
        //1、查询订单是否存在
        Orders orders = orderMapper.getById(ordersCancelDTO.getId());
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        //2、验证该订单状态(已接单）
        if (!Objects.equals(orders.getStatus(), Orders.CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        //3、验证该订单是否为已支付状态
        if (orders.getPayStatus() == Orders.PAID) {
            //todo 调用微信支付接口进行退款
            orders.setPayStatus(Orders.REFUND);
        }
        //4、修改订单状态为取消
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelTime(LocalDateTime.now());
        //5、取消原因
        orders.setCancelReason(ordersCancelDTO.getCancelReason());
        //6、修改订单数据
        orderMapper.update(orders);
    }

    /**
     * 派送订单
     * @param id
     */
    @Override
    public void delivery(Long id) {

        //1、查询订单是否存在
        Orders orders = orderMapper.getById(id);
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        //2、验证该订单状态
        if (!Objects.equals(orders.getStatus(), Orders.CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        //3、修改订单状态
        orders.setStatus(Orders.DELIVERY_IN_PROGRESS);//派送中
        //4、修改订单数据
        orderMapper.update(orders);

    }

    /**
     *  完成订单
     * @param id
     */
    @Override
    public void complete(Long id) {
        //1、查询订单是否存在
        Orders orders = orderMapper.getById(id);
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        //2、验证该订单状态
        if (!Objects.equals(orders.getStatus(), Orders.DELIVERY_IN_PROGRESS)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        //3、修改订单状态
        orders.setStatus(Orders.COMPLETED);
        //4、完成时间
        orders.setDeliveryTime(LocalDateTime.now());

        //5、修改订单数据
        orderMapper.update(orders);

    }

}

























