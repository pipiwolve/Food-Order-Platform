package com.sky.controller.admin;

import com.github.pagehelper.Page;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.service.SetMealService;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/setmeal")
@Api(tags = "套餐相关接口")
@Slf4j
public class SetMealController {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetMealService setMealService;

    @PostMapping
    @ApiOperation("新增套餐")
    @CacheEvict(cacheNames = "setmealCache", key = "#setmealDTO.categoryId")
    public Result saveWithMeal(@RequestBody SetmealDTO setmealDTO) {
        log.info("set Meal:{}", setmealDTO);
        setMealService.saveWithDish(setmealDTO);
        return Result.success();

    }

    @GetMapping("/page")
    @ApiOperation("套餐分页查询")
    public Result<PageResult> page(SetmealPageQueryDTO pageQueryDTO){
        log.info("pageQueryDTO:{}", pageQueryDTO);
        PageResult pageResult = setMealService.pageQuery(pageQueryDTO);
        return Result.success(pageResult);
    }


    @DeleteMapping
    @ApiOperation("批量删除菜品")
    @CacheEvict(cacheNames = "setmealCache", allEntries = true)
    public Result deleteWithMeal(@RequestParam List<Long> ids) {
        log.info("删除菜品:{}", ids);
        setMealService.deleteBatch(ids);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查套餐")
    @CacheEvict(cacheNames = "setmealCache", key = "#id")
    public Result<SetmealVO> findBysetmealId(@PathVariable Long id) {
        log.info("查询套餐ID:{}", id);
        SetmealVO setmealVO = setMealService.findBysetmealId(id);
        return Result.success(setmealVO);
    }

    @PutMapping
    @ApiOperation("修改套餐")
    @CacheEvict(cacheNames = "setmealCache", allEntries = true)
    public Result updateWithMeal(@RequestBody SetmealDTO setmealDTO) {
        log.info("set Meal:{}", setmealDTO);
        setMealService.updateWithMeal(setmealDTO);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation("套餐起售停售")
    @CacheEvict(cacheNames = "setmealCache", allEntries = true)
    public Result updateWithMealStatus(@PathVariable Integer status, Long id) {
        log.info("set Meal StartOrStop:{}", id);
        setMealService.startOrStop(status, id);
        return Result.success();
    }
}
