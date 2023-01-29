package com.gakki.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gakki.reggie.DTO.SetmealDto;
import com.gakki.reggie.entity.Setmeal;
import com.gakki.reggie.mapper.SetmealMapper;

import java.util.List;

public interface SetmealService extends IService<Setmeal>  {
    /**
     * 新增套餐，同时保存套餐与餐品的关联关系
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐，同时删除菜品跟套餐的关联数据
     * @param id
     */
    public void removeWithDish(List<Long> id);
}
