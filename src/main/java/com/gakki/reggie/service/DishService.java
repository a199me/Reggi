package com.gakki.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gakki.reggie.DTO.DishDto;
import com.gakki.reggie.entity.Dish;

public interface DishService extends IService<Dish> {
    //新增菜品，同时插入菜品对应的口味数据： dish  dish——flavor
    public void saveWithFlavor(DishDto dishDto);
    //根据id来查询菜品的口味和信息
    public DishDto getByIdWithFlavor(Long id);
    //更新菜品的信息（其中包含dish 跟dish_flavor）
    public void updateWithFlavor(DishDto dishDto);


}
