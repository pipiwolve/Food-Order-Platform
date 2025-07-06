package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    //涉及到数据库查询返回，mapper从xml文件中映射sql语句进行查询返回结果

    @Autowired
    private DishFlavorMapper dishFlavorMapper;


    @Autowired
    private SetmealDishMapper setmealDishMapper;
    /**
     *
     * @param dishDTO
     */

    public void saveWithFlavor(DishDTO dishDTO) {


        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish); //拷贝时候名称需要注意一致

        // 菜品表插入一条数据
        dishMapper.insert(dish);

        // 通过.xml文件中设置的 <insert id="insert" useGeneratedKeys="true" keyProperty="id">将id传入dish对象中
        Long dishId = dish.getId();


        // 风味表插入多条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            dishFlavorMapper.insertBatch(flavors);
        }

    }

    /**
     *
     * @param dishPageQueryDTO
     * @return
     */
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());

    }

    /**
     *
     * @param ids
     */

    @Transactional
    public void deleteBatch(List<Long> ids) {
        // 判断当前代码是否能够删除--是否为起售中的商品
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if(dish.getStatus() == StatusConstant.ENABLE){
               // on sale
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //判断当前菜品是否能够删除--是否与套餐关联
        List<Long> setmealIds = setmealDishMapper.getSetmealIdByDishIds(ids);
        if (setmealIds != null && setmealIds.size() > 0) {
            // on setmeal
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }

        // 根据菜品id集合批量删除菜品
        // sql: delete from dish where id in (?,?,?)
        dishMapper.deleteByIds(ids);

        // 根据菜品id集合批量删除菜品风味
        // sql: delete from dish_flavor where id in (?,?,?)
        dishFlavorMapper.deleteByDishIds(ids);

    }

    public DishVO getByIdWithFlavor(Long id){
        //得到dish对象
        Dish dish = dishMapper.getById(id);

        //得到dish_flavor对象
        List<DishFlavor> dishFlavor = dishFlavorMapper.getByDishId(id);

        //合并两者为新vo
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(dishFlavor);
        return dishVO;
    }


    public void updateWithFlavor(DishDTO dishDTO) {

        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        // 修改菜品基本信息
        dishMapper.update(dish);

        //删除原有的口味
        dishFlavorMapper.deleteByDishId(dish.getId());

        //插入新口味
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishDTO.getId());
            });
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     *
     * @param categoryId
     * @return
     */
    /*思路：首先一个套餐类(被我们通过categoryId区分)底下有许多的菜品信息，
            但是我么在套餐查询的页面有三个查询的选项
            1. 套餐名称 name
            2. 套餐分类 categoryId
            3. 售卖状态 status
            那这时候时候就变成我们三种方式可以进行传参查询，写三种方式进行传值太过冗余
            所以我们用builder方式将这三个值封装进dish对象，作为dish对象传入
            最后通过select返回的符合结果通常不是只有一条所以用List<Dish>进行接收
     */
    public List<Dish> list(Long categoryId){
        Dish dish = Dish.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        return dishMapper.list(dish);
    }

}
