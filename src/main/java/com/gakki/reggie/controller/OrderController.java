package com.gakki.reggie.controller;

import com.gakki.reggie.common.R;
import com.gakki.reggie.entity.Orders;
import com.gakki.reggie.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/order")
@RestController
@Slf4j
public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    public R<String> submit(@RequestBody Orders orders){
        orderService.submit(orders);
        return null;
    }
}
