package com.ncst.provider;

import com.ncst.rpc.enumeration.RpcError;
import com.ncst.rpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 日拱一卒，不期速成
 *
 * @Auther: i
 * @Date: 2020/10/20/17:00
 * @Description: 将用户注册的服务存储到Map中  用Map的key记录那些服务已经被注册了
 */
public class ServiceProviderImpl implements ServiceProvider {

    private static final Logger logger = LoggerFactory.getLogger(ServiceProviderImpl.class);

    //key存储服务名 value存储服务实例
    private static final Map<String,Object> serviceMap = new ConcurrentHashMap<>();
    //存储服务名
    private static final Set<String> registeredService = ConcurrentHashMap.newKeySet();

    @Override
    public <T> void addServiceProvider(T service, String serviceName) {
        if (service == null || serviceName == null) return;
        //存在
        if (registeredService.contains(serviceName)) return;
        try {
            registeredService.add(serviceName);
            serviceMap.put(serviceName,service);
            logger.info("接口: {} 注册服务： {}",service,serviceName);
        } catch (Exception e) {
            logger.error("...服务注册过程中失败...");
        }
    }

    @Override
    public Object getServiceProvider(String serviceName) {
        Object o = serviceMap.get(serviceName);
        if (o == null) {
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        return o;
    }
}
