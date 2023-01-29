package com.gakki.reggie.config;


import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//分页查询配置类
@Configuration
public class MybatisPulsConfig  {
    /**
     * 类的拦截器
     * @return
     */
    @Bean
    //Spring的@Bean注解标注在方法上，用于告诉方法去产生一个Bean对象，然后把这个Bean对象交给Spring容器来管理
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return mybatisPlusInterceptor;
    }

}
