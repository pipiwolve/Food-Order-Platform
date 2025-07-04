package com.sky.mapper;

import com.sky.entity.DishFlavor;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    // 批量插入口味数据
    void insertBatch(List<DishFlavor> flavors);

    void getPageResult(DishVO dishVO);

    /**
     * 根据菜品id来删除对应口味数据
     * @param dishId
     */
    @Delete("delete from dish_flavor where dish_id = #{dishId}")
    void deleteByDishId(Long dishId);

    void deleteByDishIds(List<Long> ids);

    /**
     * 根据菜品id求口味对象
     * @param dishId
     * @return
     */
    @Select("select * from dish_flavor where dish_id = #{dishId} ")
    List<DishFlavor> getByDishId(Long dishId);
}
