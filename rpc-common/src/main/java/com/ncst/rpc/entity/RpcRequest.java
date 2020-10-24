package com.ncst.rpc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 日拱一卒，不期速成
 *
 * @Auther: i
 * @Date: 2020/10/19/16:38
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpcRequest implements Serializable {

    /***
     * 传递的序号
     */
    private String id;

    /**
     * 接口名称 对标接口名-一个服务
     */
    private String interfaceName;

    /***
     * 方法名称 一个方法
     */
    private String methodName;

    /***
     * 方法参数
     */
    private Object [] param;

    /**
     * 参数类型
     */
    private Class<?> [] methodTypes;

    /***
     * 是否心跳
     */
    private boolean heartBeat;


}
