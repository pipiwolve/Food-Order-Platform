package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.service.SetMealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SetMealServiceImpl implements SetMealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private DishMapper dishMapper;


    @Transactional
    public void saveWithDish(SetmealDTO setmealDTO) {

        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        setmealMapper.insert(setmeal);

        Long setmealId = setmeal.getId();

       /*先获得setmeal 对象 --对应的setmeal表中元素，将前端中得到的setmealDTO数据传入，
         同时同步对dish表进行更新，一个套餐对应多道dish,且dish表与setmeal表中的关联是通过setmealId
         所以需要对每道dish加上对应套餐的setmealId，这样才能通过中间表联系起来查到
        */
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
        });

        //保存套餐和菜品键的关联关系
        setmealDishMapper.insertBatch(setmealDishes);
    }

    /**
     * 目录分页
     * @param pageQueryDTO
     * @return
     */
        /*
        思路：目的是为了分页，所以选择pagehelper进行分页，
             再通过数据库返回分页的结果
             因为最后需要的我们返回的分页结果已经被封装成PageResult的形式，包括页码,每页的分页结果
        */
    public PageResult pageQuery(SetmealPageQueryDTO pageQueryDTO) {

        PageHelper.startPage(pageQueryDTO.getPage(), pageQueryDTO.getPageSize());

        Page<SetmealVO> page = setmealMapper.pageQuery(pageQueryDTO);

//        int pageNum = pageQueryDTO.getPage();
//        int pageSize = pageQueryDTO.getPageSize();
//
//        PageHelper.startPage(pageNum, pageSize);
//        Page<SetmealVO> page = setmealMapper.pageQuery(pageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }


    /**
     * 批量删除
     * @param ids
     */
    /*
    思路：当批量删除套餐时候，我们传入的是一个setmealId的list数组，每个setmeal中还有许多dishes所以我们考虑多表间的联系
         那我们先通过的遍历ids中的id得到我们要删除的套餐对象,再对其进行判断status状态是否能够删除
         同时记得要同步删除套餐与餐品相连表中的数据---setmeal_dish
     */
    public void deleteBatch(List<Long> ids) {
        ids.forEach(id -> {
            Setmeal setmeal = setmealMapper.getById(id);
            if(StatusConstant.ENABLE == setmeal.getStatus()){
                // 起售中的商品不能删除
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
        }if(StatusConstant.DISABLE == setmeal.getStatus()){
                setmealMapper.deleteById(id);
                setmealDishMapper.deleteBySetmealId(id);
            }
        });
    }

    /**
     * 通过套餐id查套餐，用于修改页面回显数据
     * @param id
     * @return
     */
    public SetmealVO findBysetmealId(Long id){
        Setmeal setmeal = setmealMapper.getById(id);
        List<SetmealDish> setmealDishes = setmealDishMapper.getDishesBysetmealId(id);

        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    /**
     * 修改套餐
     * @param setmealDTO
     */
    @Transactional
    public void updateWithMeal(SetmealDTO setmealDTO) {
        // 第一部分对套餐表修改
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        setmealMapper.updateMeal(setmeal);

        Long setmealId = setmeal.getId();

        //第二部分对关联的dish表进行修改
        //删除套餐和菜品的关联关系，操作setmeal_dish表，执行delete
        setmealDishMapper.deleteBySetmealId(setmealId);

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
        });

        //重新插入套餐与菜品间关系
        setmealDishMapper.insertBatch(setmealDishes);
    }

    /**
     * 更改起售停售状态
     * @param id
     */
    public void startOrStop(Integer status, Long id) {
        //起售套餐时，判断套餐内是否有停售菜品
        if(status == StatusConstant.ENABLE){
            //select a.* from dish a left join setmeal_dish b on a.id = b.dish_id where b.setmeal_id = ?
            List<Dish> dishList = dishMapper.getBySetmealId(id);
            if(dishList != null && dishList.size() > 0){
                dishList.forEach(dish -> {
                    if(StatusConstant.DISABLE == dish.getStatus()){
                        throw new
                                DeletionNotAllowedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                    }
                });
            }
        }
        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status)
                .build();

        setmealMapper.updateMeal(setmeal);
    }

    /**
     * 查询套餐数据
     * @param setmeal
     * @return
     */
    @Override
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    /**
     *
     * @param id
     * @return
     */
    @Override
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }
}
