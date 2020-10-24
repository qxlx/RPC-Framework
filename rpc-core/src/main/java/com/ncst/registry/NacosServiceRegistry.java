package com.ncst.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.ncst.rpc.enumeration.RpcError;
import com.ncst.rpc.exception.RpcException;
import com.ncst.rpc.util.NacosUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * 日拱一卒，不期速成
 *
 * @Auther: i
 * @Date: 2020/10/20/17:16
 * @Description: Nacos服务注册
 */
public class NacosServiceRegistry implements ServiceRegistry{

    private static final Logger logger = LoggerFactory.getLogger(NacosServiceRegistry.class);

    @Override
    public void registry(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            //util工具类
            NacosUtil.registerService(serviceName,inetSocketAddress);
        } catch (NacosException e) {
            logger.error("...注册服务时发生错误...");
            throw new RpcException(RpcError.REGISTER_SERVICE_FAILED);
        }
    }
}
