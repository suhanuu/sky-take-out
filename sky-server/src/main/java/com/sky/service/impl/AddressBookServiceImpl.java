package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressBookServiceImpl implements AddressBookService {

    @Autowired
    private AddressBookMapper addressBookMapper;
    /**
     * 添加地址
     * @param addressBook
     */
    @Override
    public void addAddressBook(AddressBook addressBook) {

        AddressBook addressBook1 = new AddressBook();
        BeanUtils.copyProperties(addressBook, addressBook1);
        addressBook1.setUserId(BaseContext.getCurrentId());//设置当前用户id
        addressBook1.setIsDefault(0);//设置默认地址为0

        addressBookMapper.addAddressBook(addressBook1);



    }

    /**
     * 查询当前用户的地址
     * @return
     */
    @Override
    public List<AddressBook> getAddressBookList() {
        Long userId = BaseContext.getCurrentId();
        return addressBookMapper.getAddressBookList(userId);
    }

    /**
     * 查询当前用户的默认地址
     * @return
     */
    @Override
    public AddressBook getDefault() {
        Long userId = BaseContext.getCurrentId();
        Long defaultId = 1L;
        return addressBookMapper.getDefault(userId,defaultId);
    }

    /**
     * 根据id查询地址
     * @param id
     * @return
     */
    @Override
    public AddressBook getAddressBookById(Long id) {
        AddressBook addressBook = addressBookMapper.getAddressBookById(id);
        return addressBook;
    }

    /**
     * 修改地址
     * @param addressBook
     */
    @Override
    public void updateAddressBook(AddressBook addressBook) {
       addressBookMapper.update(addressBook);
    }

    /**
     * 设置默认地址
     * @param addressBook
     */
    @Override
    public void setDefault(AddressBook addressBook) {
        // 先将当前用户的所有地址的默认状态取消
        List<AddressBook> addressBooks = addressBookMapper.getAddressBookList(BaseContext.getCurrentId());
        for (AddressBook addr : addressBooks) {
            if (!addr.getId().equals(addressBook.getId())) {
                addr.setIsDefault(0);
                addressBookMapper.update(addr);
            } else {
                // 将指定地址设置为默认
                addr.setIsDefault(1);
                addressBookMapper.update(addr);
            }
        }
    }

    /**
     * 删除地址
     * @param id
     */

    @Override
    public void deleteById(Integer id) {
        addressBookMapper.delete(id);
    }
}
