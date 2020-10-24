package com.ncst.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 日拱一卒，不期速成
 *
 * @Auther: i
 * @Date: 2020/10/19/17:57
 * @Description: 服务基础扫描包
 */
@Target(ElementType.TYPE)//元素类型
@Retention(RetentionPolicy.RUNTIME)//运行时
public @interface ServiceScan {

    public String value () default "";

}
