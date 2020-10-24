package com.ncst.rpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 日拱一卒，不期速成
 *
 * @Auther: i
 * @Date: 2020/10/19/16:18
 * @Description:  序列化编号
 */
@AllArgsConstructor
@Getter
public enum SerializerCode {

    KRYO(0),//kryo
    JSON(1),//json
    HESSIAN(2),//hessian
    PROTOBUF(3);//protobuf

    private final int code;

}