package com.sky.controller.user;


import com.sky.entity.Setmeal;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userSetmealController")
@RequestMapping("/user/setmeal")
@Slf4j
@Api(tags = "C端-套餐浏览接口")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    /**
     * 根据分类id查询套餐
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询套餐")
    public Result<List<SetmealVO>> list(Setmeal setmeal) {
        log.info("查询分类id{}", setmeal.getCategoryId());
        List<SetmealVO> setmealVOList = setmealService.getCategoryById(setmeal);
        return Result.success(setmealVOList);
    }
    /**
     * 根据id查询套餐详情
     * @param id
     * @return
     */
    @GetMapping("/dish/{id}")
    @ApiOperation("根据id查询套餐详情")
    public Result<List<DishItemVO>> detail(@PathVariable Long id) {
        log.info("查询套餐详情{}", id);
        List<DishItemVO> dishItemVOList = setmealService.getSetmealDishById(id);
        return Result.success(dishItemVOList);
    }
}
