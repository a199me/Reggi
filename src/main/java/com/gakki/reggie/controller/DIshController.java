package com.gakki.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gakki.reggie.DTO.DishDto;
import com.gakki.reggie.common.R;
import com.gakki.reggie.entity.Category;
import com.gakki.reggie.entity.Dish;
import com.gakki.reggie.entity.DishFlavor;
import com.gakki.reggie.service.CategoryService;
import com.gakki.reggie.service.DishFlavorService;
import com.gakki.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */

@RestController   //@controller+@Response
@Slf4j
@RequestMapping("/dish")
public class DIshController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 将新增菜品的内容打包成json
     * @param dishDto
     * @return
     */
    @PostMapping
    //前端传回的Json数据需要我们反序列化到我们定义到的实体类中！ 用@RequestBody
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    /**
     * 菜品信息的分页
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //分页构造器
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //使用模糊查询
        queryWrapper.like(name!=null,Dish::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo,queryWrapper);
        //对象拷贝
        //页面数据只传了分类id，没显示分类名称，要显示还需以下操作
        //1.1这个操作时数据拷贝，并且不拷贝records对象，records单独操作，有dish对象变为dishDto对象
        //1.2排除records对象，自己写records。records是page对象的属性，存放查询到的数据
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        //获取List<Dish>保存的数据集合
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);


    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    //@pathVariable 就是路径变量
    public R<DishDto>get(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);

    }

    /**
     * 更新菜品的信息
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String > update(@RequestBody DishDto dishDto){
        //更新的话数据库中不仅要更新dish的表，同时要更新dishflavor表
        dishService.updateWithFlavor(dishDto);
        return  R.success("更新菜品成功");

    }

//    /**根据条件查询对应的菜品数据
////     *
////     * @param dish
////     * @return
////     */
////    @GetMapping("/list")
////    public R<List<Dish>> list(Dish dish){
////        //构造查询条件对象
////        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
////        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
////        //查询哪些状态等于1 的（起售）
////        queryWrapper.eq(Dish::getStatus,1);
////        //添加排序条件
////        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
////        //把数据传进去
////        List<Dish> list = dishService.list(queryWrapper);
////        return R.success(list);
////    }

    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        //构造查询条件对象
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        //查询哪些状态等于1 的（起售）
        queryWrapper.eq(Dish::getStatus,1);
        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        //把数据传进去
        List<Dish> list = dishService.list(queryWrapper);
        List<DishDto> dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            //当前菜品ID
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> queryWrapper1=new LambdaQueryWrapper<>();
            queryWrapper1.eq(DishFlavor::getDishId,dishId);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(queryWrapper1);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());
        return R.success(dishDtoList);
    }



}
