package com.ncst.handler;

import com.ncst.provider.ServiceProvider;
import com.ncst.provider.ServiceProviderImpl;
import com.ncst.rpc.entity.RpcRequest;
import com.ncst.rpc.entity.RpcResponse;
import com.ncst.rpc.enumeration.ResponseCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * 日拱一卒，不期速成
 *
 * @Auther: i
 * @Date: 2020/10/20/18:05
 * @Description: 进程过程调用的处理器
 */
public class RequestHandler {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private static ServiceProvider serviceProcider = null;

    static {
        serviceProcider = new ServiceProviderImpl();
    }

    //拦截器 获取rpcRequest请求
    public Object handle(RpcRequest rpcRequest) {
        //以接口为一个服务
        Object service = serviceProcider.getServiceProvider(rpcRequest.getInterfaceName());
        return invokeTargetMethod(rpcRequest, service);
    }

    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service) {
        Object result = null;
        try {
            //返回一个 方法对象反映的类或接口的 类对象表示的指定公共成员方法。
            //类名
            //方法类型 todo
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getMethodTypes());
            //todo ？ 是调用
            result = method.invoke(service, rpcRequest.getParam());
            System.out.println("【 服务 】"+rpcRequest.getInterfaceName()+"【 调用方法 】"+ rpcRequest.getMethodName());
        } catch (Exception e) {
            return RpcResponse.fail(ResponseCode.METHOD_NOT_FOUND, rpcRequest.getId());
        }
        return result;
    }


}
