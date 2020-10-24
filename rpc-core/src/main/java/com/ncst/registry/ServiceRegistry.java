package com.ncst.registry;

import java.net.InetSocketAddress;

/**
 * 日拱一卒，不期速成
 *
 * @Auther: i
 * @Date: 2020/10/20/17:12
 * @Description: 服务注册
 */
public interface ServiceRegistry {

    /****
     * 服务注册
     * @param serviceName 服务名称
     * @param inetSocketAddress ip地址
     */
    void registry (String serviceName, InetSocketAddress inetSocketAddress);

}
