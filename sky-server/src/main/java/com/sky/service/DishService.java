package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    public void saveWithFlavor(DishDTO dishDTO);

    /**
     *
     * @param dishPageQueryDTO
     * @return
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);


   void deleteBatch(List<Long> ids);

    /**
     *
     * @param id
     * @return
     */
    DishVO getByIdWithFlavor(Long id);

    /**
     *
     * @param dishDTO
     */
    void updateWithFlavor(DishDTO dishDTO);
}
