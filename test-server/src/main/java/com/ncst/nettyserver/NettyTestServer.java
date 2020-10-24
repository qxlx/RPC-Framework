package com.ncst.nettyserver;

import com.ncst.annotation.ServiceScan;
import com.ncst.serializer.CommonSerializer;
import com.ncst.transport.RpcServer;
import com.ncst.transport.server.NettyServer;

/**
 * 日拱一卒，不期速成
 *
 * @Auther: i
 * @Date: 2020/10/21/17:24
 * @Description: Netty服务提供者 服务端
 */
@ServiceScan
public class NettyTestServer {

    /*****
     *  数据流转向
     *  1.注解扫描
     *  2.发布服务
     * @param args
     */
    public static void main(String[] args) {
        RpcServer rpcServer = new NettyServer("127.0.0.1",9998, CommonSerializer.DEFAULT_SERIALIZER);
        rpcServer.start();
    }

}
