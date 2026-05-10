package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/category")
@Api(tags = "分类管理")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     * @param categoryDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增分类")
    public Result addCategory(@RequestBody CategoryDTO categoryDTO) {
        log.info("新增分类：{}", categoryDTO);
        categoryService.add(categoryDTO);
        return Result.success();
    }

    /**
     * 分类分页管理
     * @param categoryPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分类分页管理")
    public Result<PageResult> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO){
        log.info("分类分页查询：{}", categoryPageQueryDTO);
        PageResult pageResult = categoryService.pageQuery(categoryPageQueryDTO);

        return Result.success(pageResult);

    }

    /**
     * 设置启用与禁用
     * @param status
     * @param id
     * @return
     */

    @PostMapping("/status/{status}")
    @ApiOperation("设置启用与禁用")
    public Result startOrStop(@PathVariable Integer status, Long id){
        log.info("设置状态：{} id: {}", status,id);
        categoryService.startOrStop(status,id);

        return Result.success();
    }


    /**
     * 跟据类型查询分类
     * @param type
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("跟据类型查询分类")
    public Result<List<Category>>getByType(Integer type){
        log.info("查询分类：{}", type);
        List<Category> categoryList = categoryService.getByType(type);
        return Result.success(categoryList);
    }


    /**
     * 修改分类
     * @param categoryDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改分类")
    public Result update(@RequestBody CategoryDTO categoryDTO){
        log.info("修改分类{}",categoryDTO);
        categoryService.update(categoryDTO);
        return Result.success();
    }

    /**
     * 跟据id删除分类
     * @param id
     * @return
     */
    @DeleteMapping
    @ApiOperation("跟据id删除分类")
    public Result deleteById(Long id)
    {
        log.info("所要删除的id：{}",id );
        categoryService.delete(id);
        return Result.success();
    }




}
