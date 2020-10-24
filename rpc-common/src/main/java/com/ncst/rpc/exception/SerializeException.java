package com.ncst.rpc.exception;

/**
 * 日拱一卒，不期速成
 *
 * @Auther: i
 * @Date: 2020/10/19/17:07
 * @Description: 序列化异常
 */
public class SerializeException extends RuntimeException{

    public SerializeException (String msg) {
        super(msg);
    }
}
