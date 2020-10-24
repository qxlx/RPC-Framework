package com.ncst.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.ncst.loadbalancer.LoadBalancer;
import com.ncst.loadbalancer.RandomLoadBalancer;
import com.ncst.rpc.enumeration.RpcError;
import com.ncst.rpc.exception.RpcException;
import com.ncst.rpc.util.NacosUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * 日拱一卒，不期速成
 *
 * @Auther: i
 * @Date: 2020/10/20/17:33
 * @Description: Nacos服务发现 用于客户端调用
 */
public class NacosServiceDiscovery implements ServiceDiscovery{

    private static final Logger logger = LoggerFactory.getLogger(NacosServiceRegistry.class);

    private static LoadBalancer loadBalancer;

    //默认随机负载
    public NacosServiceDiscovery (LoadBalancer loadBalancer) {
        if (loadBalancer == null) {
            loadBalancer = new RandomLoadBalancer();
        } else {
            this.loadBalancer = loadBalancer;
        }
    }

    /*****
     * 根据服务名获取服务器-根据不同的策略跳转
     * @param serviceName
     * @return
     */
    @Override
    public InetSocketAddress lookUpService(String serviceName) {
        try {
            List<Instance> instanceList = NacosUtil.getAllInstance(serviceName);
            if (instanceList.size() == 0) {
                logger.error("...找不到服务实例..."+serviceName);
                throw new RpcException(RpcError.SERVICE_NOT_FOUND);
            }
            Instance instance = loadBalancer.selectForInstance(instanceList);
            return new InetSocketAddress(instance.getIp(),instance.getPort());
        } catch (NacosException e) {
            logger.info("...获取服务失败...");
        }
        return null;
    }
}
