package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealDTO;
import com.sky.entity.Dish;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    /**
     * 根据dish id 查套餐id
     * @param dishIds
     * @return
     */
    //select setmeal_id from setmeal_dish where dish_id = in(1,2,3,4)
    List<Long> getSetmealIdByDishIds(List<Long> dishIds);

    /**
     * set meal
     * @param setmealDTO
     */
    @AutoFill(OperationType.INSERT)
    void saveWithMeal(SetmealDTO setmealDTO);

    /**
     * 批量保存套餐与菜品的关联关系
     * @param setmealDishes
     */
    void insertBatch(List<SetmealDish> setmealDishes);

    /**
     * 通过setmealId删除套餐关联菜品
     * @param id
     */
    @Delete("delete from setmeal_dish where setmeal_id = #{id}")
    void deleteBySetmealId(Long id);

    /**
     * 通id查对应菜品
     * @param id
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id = #{id}")
    List<SetmealDish> getDishesBysetmealId(Long id);
}
