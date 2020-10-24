package com.ncst.registry;

import java.net.InetSocketAddress;

/**
 * 日拱一卒，不期速成
 *
 * @Auther: i
 * @Date: 2020/10/20/17:19
 * @Description: 服务发现接口
 */
public interface ServiceDiscovery {


    /****
     * 通过服务名查找
     * @param serviceName
     * @return
     */
    InetSocketAddress lookUpService (String serviceName);

}
