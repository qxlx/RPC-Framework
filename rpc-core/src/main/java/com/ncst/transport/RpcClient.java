package com.ncst.transport;

import com.ncst.rpc.entity.RpcRequest;
import com.ncst.serializer.CommonSerializer;

/**
 * 日拱一卒，不期速成
 *
 * @Auther: i
 * @Date: 2020/10/20/16:43
 * @Description: 客户端服务规范
 */
public interface RpcClient {

    /****
     * 默认初始化方式
     */
    int DEFAULT_SERIALIZER = CommonSerializer.KRYO_SERIALIZER;


    /****
     *
     * @param rpcRequest
     * @return
     */
    Object sendRequest (RpcRequest rpcRequest);
}
