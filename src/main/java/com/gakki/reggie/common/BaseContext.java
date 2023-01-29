package com.gakki.reggie.common;

/**
 * 基于ThreadLocal封装的工具类，用以保存和获取当前登录用户的id
 * 因为是静态工具类，所以不用加注解，用的时候直接调就行了，不需要Sring进行容器管理
 */
public class BaseContext {
    //应为用户id是Long类型的
    private  static ThreadLocal<Long> threadLocal=new ThreadLocal<>();
    public static void  setCurrentId(Long id){
        threadLocal.set(id);
    }
    public static Long getCurrentId(){
        return threadLocal.get();
    }



}
