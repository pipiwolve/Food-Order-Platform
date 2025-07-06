package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetMealService {

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联内容
     * @param setmealDTO
     */
    void saveWithDish(SetmealDTO setmealDTO);

    /**
     * 套餐分页
     * @param pageQueryDTO
     * @return
     */
    PageResult pageQuery(SetmealPageQueryDTO pageQueryDTO);

    /**
     * 批量删除套餐
     * @param ids
     */
    void deleteBatch(List<Long> ids);

    /**
     * 通过套餐id查套餐内容
     * @param id
     */
    SetmealVO findBysetmealId(Long id);

    /**
     *  更新套餐内容
     * @param setmealDTO
     */
    void updateWithMeal(SetmealDTO setmealDTO);

    /**
     * 更改套餐起售停售状态
     * @param status, id
     */
    void startOrStop(Integer status, Long id);
}
