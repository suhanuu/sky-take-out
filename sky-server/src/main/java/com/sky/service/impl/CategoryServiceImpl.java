package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 新增分类
     * @param categoryDTO
     */
    @Override
    public void add(CategoryDTO categoryDTO) {
        Category category = new Category();

        BeanUtils.copyProperties(categoryDTO, category);
        //默认起售状态
        category.setStatus(StatusConstant.ENABLE);

//        //创建时间和修改时间
//        category.setCreateTime(LocalDateTime.now());
//        category.setUpdateTime(LocalDateTime.now());
//
//        //创建人和修改人
//        category.setCreateUser(BaseContext.getCurrentId());
//        category.setUpdateUser(BaseContext.getCurrentId());
//        BaseContext.removeCurrentId();
        //添加分类
        categoryMapper.insert(category);

    }


    /**
     * 分类分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        PageHelper.startPage(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());
        Page<Category> page = categoryMapper.pageQuery(categoryPageQueryDTO);

        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 设置分类启用与禁用状态
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        Category category = Category.builder()
                .status(status)
                .id(id)
                .build();
//                .updateTime(LocalDateTime.now())
//                .updateUser(BaseContext.getCurrentId())


        categoryMapper.update(category);
    }

    /**
     *根据类型查询分类
     * @param type
     * @return
     */
    @Override
    public Category[] getByType(Integer type) {

        return categoryMapper.getByType(type);
    }

    @Override
    public void update(CategoryDTO categoryDTO) {
        //拷贝 数据
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        //修改时间和修改人
//        category.setUpdateTime(LocalDateTime.now());
//        category.setUpdateUser(BaseContext.getCurrentId());
        categoryMapper.update(category);
    }

    /**
     * 删除分类
     * @param id
     */
    @Transactional
    @Override
    public void delete(Long id) {
        //查询当前分类是否关联了菜品，如果关联了，则无法删除
        int dishCount = dishMapper.getDishCountByCategoryId(id);
        if (dishCount > 0) {
            //当前分类下有菜品，不能删除

            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);

        }

        //查询当前分类是否关联了套餐，如果关联了，则无法删除
        int setmealCount = setmealMapper.getSetmealCountByCategoryId(id);
        if (setmealCount > 0) {
            //当前分类下有套餐，不能删除
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }
        categoryMapper.delete(id);

    }

}
