package com.ncst.transport.client;

import com.ncst.codec.CommoDecoder;
import com.ncst.codec.CommonEncoder;
import com.ncst.serializer.CommonSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 日拱一卒，不期速成
 *
 * @Auther: i
 * @Date: 2020/10/21/16:29
 * @Description: 用于初始化对象
 */
public class ChannelProvider {

    private static final Logger logger = LoggerFactory.getLogger(ChannelProvider.class);
    private static EventLoopGroup eventLoopGroup;
    private static Bootstrap bootstrap = initBootStrap();

    //key保存String Value保存Channel
    private static Map <String,Channel> channels = new ConcurrentHashMap();


    /****
     *
     * @param socketAddress ip端口
     * @param serializer 序列化方式
     * @return
     */
    public static Channel get(InetSocketAddress socketAddress, CommonSerializer serializer) {
        //ip+序列化方式
        String key = socketAddress.toString() + serializer.getCode();
        //
        if (channels.containsKey(key)) {
            Channel channel = channels.get(key);
            if (channel != null && channel.isActive()) {
                return channel;
            } else {
                channels.remove(key);
            }
        }
        //拦截
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                //编码
                pipeline.addLast(new CommonEncoder(serializer))
                        //心跳检测
                        .addLast(new IdleStateHandler(0,5,0, TimeUnit.SECONDS))
                        //解码
                        .addLast(new CommoDecoder())
                        //拦截器
                        .addLast(new NettyClientHandler());
            }
        });
        Channel channel = null;
        try {
            channel = connect (bootstrap,socketAddress);
        }catch (Exception e) {
            logger.error("...连接客户端发生错误...",e);
            return null;
        }
        //存储到map中
        channels.put(key,channel);
        return channel;
    }

    /*****
     *
     * @param bootstrap
     * @param socketAddress
     * @return
     */
    private static Channel connect(Bootstrap bootstrap, InetSocketAddress socketAddress) throws ExecutionException, InterruptedException {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(socketAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                logger.info("...客户端连接成功...");
                completableFuture.complete(future.channel());
            } else {
                throw new IllegalAccessException();
            }
        });
        return completableFuture.get();
    }

    /*****
     * 初始化bootStrap
     * @return
     */
    public static Bootstrap initBootStrap() {
        eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                //连接时间
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                //开启TCP 底层心跳
                .option(ChannelOption.SO_KEEPALIVE, true)
                //TCP默认开启了 Nagle 算法，该算法的作用是尽可能的发送大数据快，减少网络传输。
                //TCP_NODELAY 参数的作用就是控制是否启用 Nagle 算法。
                .option(ChannelOption.TCP_NODELAY, true);
        return bootstrap;
    }


}
