package com.ncst.rpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 日拱一卒，不期速成
 *
 * @Auther: i
 * @Date: 2020/10/19/16:17
 * @Description: 自定义数据协议中 PackageType 表名是一个请求还是响应
 */
@AllArgsConstructor
@Getter
public enum PackageType {

    REQUEST_PACK(0),//请求
    RESPONSE_PACK(1);//响应

    private final int code;

}
