package com.ncst.rpc.exception;

import com.ncst.rpc.enumeration.RpcError;

/**
 * 日拱一卒，不期速成
 *
 * @Auther: i
 * @Date: 2020/10/19/16:59
 * @Description: RPC调用异常
 */
public class RpcException extends RuntimeException {

    public RpcException (RpcError rpcError, String detail) {
        super(rpcError.getMessage() +" : "+ detail);
    }

    public RpcException(String message,Throwable cause) {
        super(message,cause);
    }

    public RpcException(RpcError rpcError) {
        super(rpcError.getMessage());
    }

}
