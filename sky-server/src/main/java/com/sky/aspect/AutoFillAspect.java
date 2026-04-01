package com.sky.aspect;

import com.sky.annotion.AutoFill;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;


// 自定义切面类
    @Aspect
    @Component
    @Slf4j
    public class AutoFillAspect {
    /**
     * 切入点
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotion.AutoFill)")
    public void autoFillPointCut() {
    }

    /**
     * 前置通知
     */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) {
        log.info("开始进行数据填充");
        //获取当前拦截方法上的数据库操作类型
        OperationType oper = joinPoint.getTarget().getClass().getAnnotation(AutoFill.class).value();

        // 获取方法参数，并填充创建时间、更新时间
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return;
        }
        Object object = args[0];
        //根据对应的数据库操作类型，为对应的属性赋值
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();
        // 根据操作类型，为对应的属性赋值
        if ( oper == OperationType.INSERT) {
            // 为4 个公共属性赋

            try {
                object.getClass().getMethod("setCreateTime", LocalDateTime.class).invoke(object, now);
                object.getClass().getMethod("setUpdateTime", LocalDateTime.class).invoke(object, now);
                object.getClass().getMethod("setCreateUser", Long.class).invoke(object, currentId);
                object.getClass().getMethod("setUpdateUser", Long.class).invoke(object, currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (oper == OperationType.UPDATE) {
            try {
                object.getClass().getMethod("setUpdateTime", LocalDateTime.class).invoke(object, now);
                object.getClass().getMethod("setUpdateUser", Long.class).invoke(object, currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}


