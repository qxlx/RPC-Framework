package com.ncst.transport;

import com.ncst.annotation.Service;
import com.ncst.annotation.ServiceScan;
import com.ncst.provider.ServiceProvider;
import com.ncst.registry.ServiceRegistry;
import com.ncst.rpc.enumeration.RpcError;
import com.ncst.rpc.exception.RpcException;
import com.ncst.rpc.util.ReflectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Set;

/**
 * 日拱一卒，不期速成
 *
 * @Auther: i
 * @Date: 2020/10/21/10:32
 * @Description: 抽象RPC服务基类
 */
public abstract class AbstractRpcServer implements RpcServer {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractRpcServer.class);

    //ip地址
    protected String host;
    //port
    protected Integer port;

    //服务提供
    protected ServiceProvider serviceProvider;
    //服务注册
    protected ServiceRegistry serviceRegistry;

    /****
     * 扫描带有Service的类
     */
    public void scanServices() {
        //main方法所在类
        String mainClassName = ReflectUtil.getStackTrace();
        Class<?> startClass = null;

        try {
            //加载类-main方法
            startClass = Class.forName(mainClassName);
            //如果注解上没有ServiceScan
            if (!startClass.isAnnotationPresent(ServiceScan.class)) {
                logger.error("...启动类缺少ServiecScan...");
                throw new RpcException(RpcError.SERVICE_SCAN_PACKAGE_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("...发生错误...");
            throw new RpcException(RpcError.UNKNOWN_ERROR);
        }
        //获取扫描基础包
        String basePackage = startClass.getAnnotation(ServiceScan.class).value();
        //如果等于"" 全扫描 截取类名
        if ("".equals(basePackage)) {
            basePackage = mainClassName.substring(0, mainClassName.lastIndexOf("."));
        }

        //获取对应的类
        Set<Class<?>> classSet = ReflectUtil.getClasses(basePackage);
        //loop
        for (Class<?> clazz : classSet) {
            //获取被Service注解的类
            if (clazz.isAnnotationPresent(Service.class)) {
                //服务名
                String serviceName = clazz.getAnnotation(Service.class).name();
                Object obj;
                try {
                    obj = clazz.newInstance();
                }catch (Exception e){
                    logger.error("创建"+clazz+"发生错误");
                    //如果当前出现错误 跳过
                    continue;
                }
                if ("".equals(serviceName)) {
                    Class<?> [] interfaces = clazz.getInterfaces();
                    for (Class<?>  oneInterface : interfaces) {
                        publicServer(obj,oneInterface.getCanonicalName());
                    }
                } else {
                    //发布服务
                    publicServer(obj,serviceName);
                }
            }
        }
    }

    /****
     *
     * @param server 服务
     * @param serviceName 服务名
     * @param <T>
     */
    @Override
    public <T> void publicServer(T server, String serviceName) {
        //服务提供
        serviceProvider.addServiceProvider(server, serviceName);
        //服务注册
        serviceRegistry.registry(serviceName, new InetSocketAddress(host, port));
    }
}
