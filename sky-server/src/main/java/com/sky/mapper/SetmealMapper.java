package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealMapper {

    /**
     * 分页返回查询结果
     *
     * @param pageQueryDTO
     * @return
     */
   Page<SetmealVO> pageQuery(SetmealPageQueryDTO pageQueryDTO);

    /**
     * 根据分类id查询套餐的数量
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    /**
     * set meal
     * @param setmeal
     */
    @AutoFill(OperationType.INSERT)
    void insert(Setmeal setmeal);


    /**
     * 通过setmealId查询套餐
     * @param id
     * @return
     */
    @Select("select * from setmeal where id = #{id}")
    Setmeal getById(Long id);

 /**
  * 通过id删除
  * @param setMealId
  */
 @Delete("delete from setmeal where id = #{setMealId}")
  void deleteById(Long setMealId);

 /**
  * 更新套餐
  * @param setmeal
  */
 void updateMeal(Setmeal setmeal);

 /**
  * 更新起售停售状态
  * @param id
  */
 void updateStatus(Integer id);
}
