package com.gakki.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gakki.reggie.entity.User;
import com.gakki.reggie.mapper.UserMapper;
import com.gakki.reggie.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>implements UserService {

}
