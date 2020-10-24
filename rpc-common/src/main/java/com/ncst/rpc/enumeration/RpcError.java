package com.ncst.rpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 日拱一卒，不期速成
 *
 * @Auther: i
 * @Date: 2020/10/19/16:18
 * @Description: Rpc错误
 */
@AllArgsConstructor
@Getter
public enum RpcError {

    //unknown_error
    UNKNOWN_ERROR("出现未知错误"),
    //service_sacn_packeg_not_fonud
    SERVICE_SCAN_PACKAGE_NOT_FOUND("启动类ServiceScan注解缺失"),
    //client_connect_seerver_failure
    CLIENT_CONNECT_SERVER_FAILURE("客户端连接服务端失败"),
    //service_invocation_failure
    SERVICE_INVOCATION_FAILURE("服务调用出现失败"),
    //service_not_found
    SERVICE_NOT_FOUND("找不到对应的服务"),
    //service_not_implement_any_interface
    SERVICE_NOT_IMPLEMENT_ANY_INTERFACE("注册的服务未实现接口"),
    //unknown_protocol
    UNKNOWN_PROTOCOL("不识别的协议包"),
    //unknown_serializer
    UNKNOWN_SERIALIZER("不识别的(反)序列化器"),
    //unknown_package_type
    UNKNOWN_PACKAGE_TYPE("不识别的数据包类型"),
    //serializer_not_found
    SERIALIZER_NOT_FOUND("找不到序列化器"),
    //response_not_match
    RESPONSE_NOT_MATCH("响应与请求号不匹配"),
    //failed_to_connect_to_service_registry
    FAILED_TO_CONNECT_TO_SERVICE_REGISTRY("连接注册中心失败"),
    //regsiter_service_failed
    REGISTER_SERVICE_FAILED("注册服务失败");

    private final String message;

}