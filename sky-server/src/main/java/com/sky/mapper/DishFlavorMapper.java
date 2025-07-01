package com.sky.mapper;

import com.sky.entity.DishFlavor;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    // 批量插入口味数据
    void insertBatch(List<DishFlavor> flavors);

    void getPageResult(DishVO dishVO);
}
