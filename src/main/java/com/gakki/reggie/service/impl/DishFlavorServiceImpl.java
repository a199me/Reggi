package com.gakki.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gakki.reggie.entity.DishFlavor;
import com.gakki.reggie.mapper.DIshFlavorMapper;
import com.gakki.reggie.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DIshFlavorMapper, DishFlavor> implements DishFlavorService {
}
