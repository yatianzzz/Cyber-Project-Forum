package com.example.system.common;

// Get user id
public class BaseContext {

    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    //  set id
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
