package com.iecube.iecubetutorial.Auth;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 支持多角色的权限控制注解
 * role: SUPER ADMIN OPERATOR USER_M USER
 * 使用示例：
 * @ ApiPermissions({"USER", "ADMIN"})  允许用户和管理员访问
 * @ ApiPermissions({"SUPER", "ADMIN"}) 允许超级管理员和管理员访问
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiPermissions {
    String[] value() default {};
}
