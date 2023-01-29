package com.gakki.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gakki.reggie.common.CustomException;
import com.gakki.reggie.entity.Category;
import com.gakki.reggie.entity.Dish;
import com.gakki.reggie.entity.Setmeal;
import com.gakki.reggie.mapper.CategoryMapper;
import com.gakki.reggie.service.CategoryService;
import com.gakki.reggie.service.DishService;
import com.gakki.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    /**
     * 根据id来删除分类，删除之前判断菜品跟相应套餐是否关联
     * @param id
     */
    @Override
    public void remove(Long id) {

        LambdaQueryWrapper<Dish> dishqueryWrapper=new LambdaQueryWrapper();
        //查询当前分类是否关联了菜品，如果已经关联，那么抛出业务异常
        dishqueryWrapper.eq(Dish::getCategoryId,id);
        int count = dishService.count(dishqueryWrapper);

        if (count>0){
            //已经关联菜品，抛出异常
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }
        //查询当前分类是否关联了套餐，如果已经关联，那么抛出异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper=new LambdaQueryWrapper<>();
        //添加条件查询，根据跟类id进行查询
         setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        int count2=setmealService.count(setmealLambdaQueryWrapper);
        if (count2>0){
            //已经关联了套餐，抛出业务异常
            throw new CustomException("当前已经关联了套餐，不能删除");
        }

        //正常删除分类
        super.removeById(id);
    }
}
