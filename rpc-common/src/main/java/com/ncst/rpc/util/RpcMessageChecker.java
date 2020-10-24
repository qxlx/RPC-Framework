package com.ncst.rpc.util;

import com.ncst.rpc.entity.RpcRequest;
import com.ncst.rpc.entity.RpcResponse;
import com.ncst.rpc.enumeration.ResponseCode;
import com.ncst.rpc.enumeration.RpcError;
import com.ncst.rpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 检查响应与请求
 *
 * @author i
 */
public class RpcMessageChecker {

    public static final String INTERFACE_NAME = "interfaceName";
    private static final Logger logger = LoggerFactory.getLogger(RpcMessageChecker.class);

    private RpcMessageChecker() {
    }

    public static void check(RpcRequest rpcRequest, RpcResponse rpcResponse) {
        if (rpcResponse == null) {
            logger.error("...调用服务失败,serviceName:{}...", rpcRequest.getInterfaceName());
            throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }
        //请求ID和响应ID不是同一个
        if (!rpcRequest.getId().equals(rpcResponse.getRequestId())) {
            throw new RpcException(RpcError.RESPONSE_NOT_MATCH, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }
        //状态码非200
        if (rpcResponse.getStatusCode() == null || !rpcResponse.getStatusCode().equals(ResponseCode.SUCCESS.getCode())) {
            logger.error("调用服务失败,serviceName:{},RpcResponse:{}", rpcRequest.getInterfaceName(), rpcResponse);
            throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }
    }

}
