package com.gakki.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gakki.reggie.Utils.SMSUtils;
import com.gakki.reggie.Utils.ValidateCodeUtils;
import com.gakki.reggie.common.R;
import com.gakki.reggie.entity.User;
import com.gakki.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    /**
     * 发送短信验证码
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        // 获取手机号
        String phone = user.getPhone();
        if(StringUtils.isNotEmpty(phone)){
            //生成随机验证码
            String code = ValidateCodeUtils.generateValidateCode(6).toString();
            log.info("code= {}",code);
            //调用阿里云提供的短信服务API完成发送短信(调用工具类)
            SMSUtils.sendMessage("HongkongDoll","",phone,code);
            //需要将生成的验证码哦保存到session
            session.setAttribute(phone,code);
            return R.success("手机发送验证码成功");
        }
       return R.error("短信发送失败");
    }

    /**
     * 移动端用户登录
     * @param
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User>login(@RequestBody Map map, HttpSession session){
        log.info(map.toString());
        //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();
        //从Session中获取保存的验证码
        Object codeInsession = session.getAttribute(phone);
        //进行验证码的比对（页面提交的验证码和Session中保存的验证码）
        if (codeInsession!=null&&codeInsession.equals(code)){

            //如果能对比成功，说明登录成功

            //判断当前手机号对应用户是否为新用户，如果为新用户，自动完成注册
            LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);
            if (user==null){
                user=new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            return R.success(user);
        }

        return R.error("登录失败");
    }
}
