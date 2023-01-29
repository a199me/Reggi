package com.gakki.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gakki.reggie.DTO.DishDto;
import com.gakki.reggie.entity.Dish;
import com.gakki.reggie.entity.DishFlavor;
import com.gakki.reggie.mapper.DishMapper;
import com.gakki.reggie.service.DishFlavorService;
import com.gakki.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;
    /**
     * 新增菜品的同时保存对应的口味数据
     * @param dishDto
     */
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表
        this.save(dishDto);
        Long dishId = dishDto.getId();//菜品id
        //菜品口味，save已经将菜品的基本信息保存到了数据库中了，把数据苦衷保存的菜品口味赋值给List
        List<DishFlavor> flavor=dishDto.getFlavors();
        //保存菜品口味数据到菜品表
        dishFlavorService.saveBatch(dishDto.getFlavors());
        //通过stream流来进行循环
        flavor.stream().map(item->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

    }

    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //就是查dish和dishflovr ，然后组合成DishDto

        //查询菜品基本信息，从dish查询(dish是本表，直接从里边查就行)
        Dish dish = this.getById(id);
        //数据拷贝
        DishDto dishDto=new DishDto();
        //把dish转换成dishDto
        BeanUtils.copyProperties(dish,dishDto);

        //查询当前口味信息,从dish_flavor表查询
        LambdaQueryWrapper<DishFlavor>queryWrapper=new LambdaQueryWrapper<>();
        //不需要添加条件的dish，将DishFlaovr的dishid 跟 dish表中的id进性对比，这里没有添加其他条件
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        //获取flavor中的数据
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);
        return dishDto;
    }

    //更新菜品信息，同时更新口味信息 ,添加事务注解，保证数据的一致性
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //首先更新dish信息。
        this.updateById(dishDto);
        //更新口味信息（dishid是关于口味的信息），可以先清理口味的信息（dishid）然后再重新添加当前提交过来的信息
          //1.想要清楚dishid里的内容，先要查找dishid，
        LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        //查找到之后把querWrapper传到remove中，这样就清除掉了
        dishFlavorService.remove(queryWrapper);
         //清理完毕之后重新插入信息，先拿到口味数据
        List<DishFlavor> flavors = dishDto.getFlavors();
        dishFlavorService.saveBatch(flavors);
        //封装DishFlavor的时候只是封装了dish 的name跟value 并没有封装dishId，也就是说dishId没有值
        //就是说你新增一个口味时，这个口味是没有dishId的，你修改的口味本来就拥有dishId
        //解决方法，把flavors拿出来，把每一项都拿出来，每一项都set dishId
        flavors=flavors.stream().map((item)->{
            item.setId(IdWorker.getId());
            return  item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);






    }
}
