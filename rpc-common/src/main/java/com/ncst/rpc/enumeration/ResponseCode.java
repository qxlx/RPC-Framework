package com.ncst.rpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 日拱一卒，不期速成
 *
 * @Auther: i
 * @Date: 2020/10/19/16:17
 * @Description:  响应状态码
 */
@AllArgsConstructor
@Getter
public enum ResponseCode {

    SUCCESS(200, "调用方法成功"),//成功
    FAIL(500, "调用方法失败"), //失败
    METHOD_NOT_FOUND(500, "未找到指定方法"), //没有找到对应的方法
    CLASS_NOT_FOUND(500, "未找到指定类");//没有找到对应的类

    private final int code;
    private final String message;

}
