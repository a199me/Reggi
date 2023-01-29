package com.gakki;

import com.gakki.reggie.common.JacksonObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.cbor.MappingJackson2CborHttpMessageConverter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.List;

//@Slf4j 日志,加上这个注解之后就可以在使用loh方法了
@Slf4j
@SpringBootApplication
@EnableTransactionManagement  //开启事务注解的支持
//为了让过滤器生效，要加注解
@ServletComponentScan
public class ReggiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReggiApplication.class, args);
        log.info("项目启动成功");
    }

}
