package com.ncst.transport.client;

import com.ncst.loadbalancer.LoadBalancer;
import com.ncst.loadbalancer.RandomLoadBalancer;
import com.ncst.loadbalancer.RoundRobinLoadBalancer;
import com.ncst.registry.NacosServiceDiscovery;
import com.ncst.registry.ServiceDiscovery;
import com.ncst.rpc.entity.RpcRequest;
import com.ncst.rpc.entity.RpcResponse;
import com.ncst.rpc.enumeration.RpcError;
import com.ncst.rpc.exception.RpcException;
import com.ncst.rpc.factory.SingletonFactory;
import com.ncst.serializer.CommonSerializer;
import com.ncst.transport.RpcClient;
import com.ncst.transport.server.NettyServer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

/**
 * 日拱一卒，不期速成
 *
 * @Auther: i
 * @Date: 2020/10/21/15:36
 * @Description: Netty客户端
 */
public class NettyClient implements RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private static final EventLoopGroup workGroup;
    private static final Bootstrap bootStrap;

    static {
        workGroup = new NioEventLoopGroup();
        bootStrap = new Bootstrap();
        bootStrap.group(workGroup)
                .channel(NioSocketChannel.class);
    }

    private UnprocessedRequests unprocessedRequests;
    private CommonSerializer commonSerializer;
    private ServiceDiscovery serviceDiscovery;
    private LoadBalancer loadBalancer;


    public NettyClient() {
        this(DEFAULT_SERIALIZER, new RandomLoadBalancer());
    }

    //默认随机负载
    public NettyClient(int serializerCode) {
        this(serializerCode, new RoundRobinLoadBalancer());
    }

    //自定义负载
    public NettyClient(LoadBalancer loadBalancer) {
        this(DEFAULT_SERIALIZER, loadBalancer);
    }

    public NettyClient(int serializerCode, LoadBalancer loadBalancer) {
        commonSerializer = CommonSerializer.getBytes(serializerCode);
        unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
        serviceDiscovery = new NacosServiceDiscovery(loadBalancer);
    }

    /****
     *  客户端发送数据
     * @param rpcRequest
     * @return
     */
    @Override
    public CompletableFuture<RpcResponse> sendRequest(RpcRequest rpcRequest) {
        if (commonSerializer == null) {
            logger.error("【...没有序列化...】");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        CompletableFuture<RpcResponse> resultFuture = new CompletableFuture<>();
        try {
            //根据服务名查询某一个台服务
            InetSocketAddress inetSocketAddress = serviceDiscovery.lookUpService(rpcRequest.getInterfaceName());
            //根据ip信息和序列化方式查找通道
            Channel channel = ChannelProvider.get(inetSocketAddress, commonSerializer);
            //如果通道没有激活 直接关闭
            if (!channel.isActive()) {
                workGroup.shutdownGracefully();
                return null;
            }
            //存放未处理的请求
            unprocessedRequests.put(rpcRequest.getId(), resultFuture);
            //将数据写入到通道里 并设置一个监听器
            channel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    logger.info(String.format("【客户端发送消息: %s】", rpcRequest.toString()));
                } else {
                    future.channel().close();
                    resultFuture.completeExceptionally(future.cause());
                    logger.error("【发送消息时有错误发生: 】", future.cause());
                }
            });

        } catch (Exception e) {
            unprocessedRequests.remove(rpcRequest.getId());
            logger.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
        return resultFuture;
    }
}
