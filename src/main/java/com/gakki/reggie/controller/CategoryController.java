package com.gakki.reggie.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gakki.reggie.common.R;
import com.gakki.reggie.entity.Category;
import com.gakki.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController  {
    @Autowired
    private CategoryService categoryService;

    /**
     * 给菜品新增分类
     * @return
     */
    @PostMapping
    //@Requestbodyy是作用在形参列表上，
    // 用于将前台发送过来固定格式的数据【xml 格式或者 json等】封装为对应的 JavaBean 对象，
    // 封装时使用到的一个对象是系统默认配置的 HttpMessageConverter进行解析，然后封装到形参上。
    public R<String> save(@RequestBody  Category category){
        log.info("category: {}",category);
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){
        //分页构造器
        Page<Category> pageInfo=new Page<>(page,pageSize);
        //条件构造函数
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();
        //添加一个排序条件，根据sort来进行排序
        queryWrapper.orderByAsc(Category::getSort);
        //执行
        categoryService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);

    }
    @DeleteMapping
    public R<String> delete(Long id){
        log.info("删除分类，id为： {}",id);
       // categoryService.removeById(id);
        categoryService.remove(id);
        return R.success("删除分类成功！");
    }

    /**
     * 根据id修改分类信息
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("修改分类信息 {}",category);
        categoryService.updateById(category);
        return R.success("修改分类信息成功");

    }

    /**
     * 根据条件来查询分类数据
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        //添加排序条件,优先使用sort来排序，如果sort时间相同的话就用updatetime时间来排
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);


    }

}
