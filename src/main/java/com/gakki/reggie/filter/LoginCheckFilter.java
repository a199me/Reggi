package com.gakki.reggie.filter;
//拦截器，检查用户是否已经登录，未登录是不能直接进入主界面的


import com.alibaba.fastjson.JSON;
import com.gakki.reggie.common.BaseContext;
import com.gakki.reggie.common.R;
import com.sun.org.apache.xpath.internal.objects.XBoolean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
//名称起什么名都可以，这里跟文件 保持一致， 重点是路径。这里是所有的请求都拦截,但是要记得给静态资源放行
@Slf4j
@WebFilter(filterName="loginCheckFilter",urlPatterns ="/*" )
//过滤器要实现接口Filter
public class LoginCheckFilter implements Filter {

    //这个是专门用来进行路径比较的（路径匹配器）
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //向下转型，把Servletrequest 转型为http类型的
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //1.获取本次请求的URL
        String requestURI = request.getRequestURI();
        //2.判断本次请求是否需要处理，定义一些请求路径，如果是这些路径的话，过滤器不会拦截
        String[] urls = new String[]{
                //目前这些路径都是在controller层定义的。
                //用户登录的请求
                "/employee/login",
                //退出系统的请求
                "/employee/logout",
                //静态支援直接放过去
                "/backend/**",
                "/front/**",
                //不登陆也能访问到上传页面
                "/common/**",
                "user/sendMsg",
                "user/login"
        };

        //2.1 判断本次请求是否需要处理，调用check方法。
        boolean check=check(urls,requestURI);

        //3.不需要处理，则直接放行
        if (check){
            log.info("本次请求不需要处理 {}"+requestURI);
            //进行放行
            filterChain.doFilter(request, response);
            return;
        }
        //4-1.判断登录状态，如果已经登录，则直接放行 ,getAttribute：获得登录用户的状态
        //如果查询到用户的状态不为空，那就说明，用户已经登录，可以放行
        if (request.getSession().getAttribute("employee")!=null){
            log.info("用户已登录  {}"+request.getSession().getAttribute("employee"));
            //已经登录了，所以从这里获取它的id，用ThreadLocal获取
            Long empId =(Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);
            filterChain.doFilter(request, response);
            return;
        }

        //4-2.判断移动端登录状态，如果已经登录，则直接放行 ,getAttribute：获得登录用户的状态
        //如果查询到用户的状态不为空，那就说明，用户已经登录，可以放行
        if (request.getSession().getAttribute("user")!=null){
            log.info("用户已登录  {}"+request.getSession().getAttribute("user"));
            //已经登录了，所以从这里获取它的id，用ThreadLocal获取
            Long userId =(Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request, response);
            return;
        }
        //5.如果未登录则返回未登录结果，通过输出流方式向客户端页面响应数据、
        log.info("用户未登录 {}");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    //检查本次请求是否需要放行

    /**
     *
     */
    public boolean check(String[] urls, String requestURI) {
        for (String url : urls) {

            boolean match = PATH_MATCHER.match(url, requestURI);
            //如果返回true的话，就是匹配上了
            if (match){
                return true;
            }
        }
        //如果全都循环了一遍也没有返回，那就说明匹配不上，返回false
        return  false;
    }
}

