package com.ncst.transport;

import com.ncst.rpc.entity.RpcRequest;
import com.ncst.rpc.entity.RpcResponse;
import com.ncst.rpc.util.RpcMessageChecker;
import com.ncst.transport.client.NettyClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * 日拱一卒，不期速成
 *
 * @Auther: i
 * @Date: 2020/10/21/19:48
 * @Description: 客户端代理
 */
public class RpcClientProxy implements InvocationHandler {

    private static final Logger logger = LoggerFactory.getLogger(RpcClientProxy.class);
    private final RpcClient rpcClient;

    public RpcClientProxy (RpcClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    /*****
     * 生成代理类
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this::invoke);
    }

    /*****
     * 动态生成的类 一旦方法调用就触发invoke进行拦截处理
     * @param proxy
     * @param method
     * @param args
     * @return
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        logger.info("调用服务: {}-----{}", method.getDeclaringClass().getName(), method.getName());
        //生成rpcRequest对象、id UUID、类名=服务名、方法名、参数、参数类型、是否心跳false
        RpcRequest rpcRequest = new RpcRequest(UUID.randomUUID().toString(), method.getDeclaringClass().getName(),
                method.getName(), args, method.getParameterTypes(), false);
        RpcResponse rpcResponse = null;
        //如果Netty形式
        if (rpcClient instanceof NettyClient) {
            try {
                //异步调用-发送数据给服务端
                CompletableFuture<RpcResponse> completableFuture =
                        (CompletableFuture<RpcResponse>) rpcClient.sendRequest(rpcRequest);
                //异步获取数据  等待，如果必要的，为这个未来完成，然后返回其结果
                rpcResponse = completableFuture.get();
            } catch (Exception e) {
                logger.error("方法调用请求发送失败", e);
                e.printStackTrace();
                return null;
            }
        }
        //判断
        RpcMessageChecker.check(rpcRequest, rpcResponse);
        return rpcResponse.getData();
    }

}
