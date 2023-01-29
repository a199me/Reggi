package com.gakki.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gakki.reggie.DTO.DishDto;
import com.gakki.reggie.common.R;
import com.gakki.reggie.entity.Dish;
import com.gakki.reggie.entity.Employee;
import com.gakki.reggie.service.CategoryService;
import com.gakki.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
//import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.lang.StringUtils;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController  //可以理解为@Controller +@ResponseBody(将java对象转化为json格式)
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private EmployeeService employeeService;
    @PostMapping("/login")
    //因为登录的时候需要用json数据格式，所以参数要交@RequestBody注解,
    // 第一一个参数，就是说如果登录成功，那么就将employee 的id 保存到session一份，这样如果想获取登录成功的用户就可以随时获取。
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        /*
        1.将页面提交的密码password进行md5加密
        */
        String password = employee.getPassword();
        password= DigestUtils.md5DigestAsHex(password.getBytes());
        /*
         * 2.查询数据库：根据页面提交的用户名username查询数据库*/
        //lambda匿名函数:LambdaQueryWrapper ：与QueryWrapper查询类似，不过使用的是Lambda语法
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());//一个是数据库中拥有的username，另外一个是用户输入的username，二者进行对比
        Employee emp = employeeService.getOne(queryWrapper); //使用getOne：因为user_name字段有unique唯一约束，不会出现查询出多个结果,把比对完之后的值赋给emp

        /*
         * 3.如果没有查询到返回登录失败的结果*/
        if (emp == null) {
            return R.error("登陆失败");
        }
        /*
         * 4.密码对比，如果没有查询到则返回登陆失败结果*/
        if (!emp.getPassword().equals(password)){//emp.getPassword()获取的是数据库中的密码，password的密码是用户输入的密码

            return R.error("登陆失败");
        }
        /*5。查看员工的状态 0表示禁用，1表示状态可用
        * */
        if (emp.getStatus()==0){
        return R.error("账号已禁用");
        }
        /*6.登陆成功，将员工id存入Session并返回登陆成功结果*/
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }


    @PostMapping("logout")
    /*写一个退出的方法*/
    public R<String> logout(HttpServletRequest request){
        //清理Session中登录成功的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     *新增员工
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Employee employee,HttpServletRequest request){
        //先测试一下前端能否请求
        log.info("新增员工，员工信息： {}",employee.toString());
        //给新增员工设置个初始密码123456
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
         //手动设置其他员工信息
        //员工创建时间以及更新时间
        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());
        //设置更新员工的管理员信息
       // Long empid= (Long) request.getSession().getAttribute("employee");
       // employee.setCreateUser(empid);
       // employee.setUpdateUser(empid);
        employeeService.save(employee);
        return R.success("新增员工成功");
    }
    //员工信息的分页查询
    @GetMapping("/page")
    public R<Page>page(int page, int pageSize, String name){
        log.info("page= {},pageSize= {},name= {}",page,pageSize,name);

        //分页构造器，通过page来进行构造
        Page<Employee> pageInfo=new Page(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Employee> queryWrapper=new LambdaQueryWrapper();
        //添加过滤条件,Employee::getName是mybatis-plus的用法，相当于表的字段名。
        //根据name字段进行查询，当我们name不等于空的时候添加
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        //添加排序条件,根据时间进行降序排序
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageInfo,queryWrapper);

        return  R.success(pageInfo);

    }

    /**
     * 修改用户状态
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Employee employee,HttpServletRequest request){
        Long employee1 =(Long) request.getSession().getAttribute("employee");
        employee.setUpdateUser(employee1);
        employee.setUpdateTime(LocalDateTime.now());
        employeeService.updateById(employee);
        return  R.success("修改员工信息成功！");
    }

    /**
     * 根据id来查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据id查询数据信息");
        Employee byId = employeeService.getById(id);
        if (byId!=null){
            return R.success(byId);
        }
        return R.error("没有查询到对应员工信息");
    }
}




