package cn.com.ava.common.util;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Activity,Fragment参数Extra注解
 *
 * <code>
 * @Extra("key")
 * private String key;
 * </code>
 *
 * */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Extra {

    String value() default "";
}
