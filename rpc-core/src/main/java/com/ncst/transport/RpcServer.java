package com.ncst.transport;

import com.ncst.serializer.CommonSerializer;

/**
 * 日拱一卒，不期速成
 *
 * @Auther: i
 * @Date: 2020/10/19/18:12
 * @Description:  服务端接口规范
 */
public interface RpcServer {

    /****
     * 默认序列化方式
     */
    int DEFAULT_SERIAIIZER = CommonSerializer.KRYO_SERIALIZER;

    /****
     * 启动
     */
    void start ();

    /****
     * 发布服务
     * @param server 服务
     * @param serviceName 服务名
     * @param <T>
     */
    <T> void publicServer(T server,String serviceName);

}
