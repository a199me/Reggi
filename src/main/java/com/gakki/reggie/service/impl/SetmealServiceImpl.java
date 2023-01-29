package com.gakki.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gakki.reggie.DTO.SetmealDto;
import com.gakki.reggie.entity.Dish;
import com.gakki.reggie.entity.Setmeal;
import com.gakki.reggie.entity.SetmealDish;

import com.gakki.reggie.mapper.SetmealDishMapper;
import com.gakki.reggie.mapper.SetmealMapper;
import com.gakki.reggie.service.SetmealDishService;

import com.gakki.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDish;
    /**
     * 因为要操作两张表，所以需要加入事务注解，要么全成功，要么全失败
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息，操作Setmeal这张表，执行insert操作
        this.save(setmealDto);
        //保存套餐和菜品的关联信息，操作setmeal_dish 执行insert操作
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map(item->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDish.saveBatch(setmealDishes);
    }

    @Override
    @Transactional
    public void removeWithDish(List<Long> id) {
        //select count(*) from setmeal where id in(1,2,3)and status =1
        //查询套餐状态确定是否可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper();
        queryWrapper.in(Setmeal::getId,id);
        queryWrapper.eq(Setmeal::getStatus,1);
        int count = this.count(queryWrapper);
        if (count>0){
            //如果不能删除，排除业务异常
            throw new ClassCastException("套餐正在售卖中，不能删除。");
        }


        //如果可以删除，先删除关系表中的数据-setmeal
        this.removeByIds(id);

        //删除关系表中的数据 delete from setmeal dish where
        LambdaQueryWrapper<SetmealDish> queryWrapper1=new LambdaQueryWrapper<>();
        queryWrapper1.in(SetmealDish::getSetmealId,id);
        setmealDish.remove(queryWrapper1);
    }

}
