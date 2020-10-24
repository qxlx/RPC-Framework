package com.ncst.transport.server;

import com.ncst.codec.CommoDecoder;
import com.ncst.codec.CommonEncoder;
import com.ncst.hook.ShutDownHook;
import com.ncst.provider.ServiceProviderImpl;
import com.ncst.registry.NacosServiceRegistry;
import com.ncst.serializer.CommonSerializer;
import com.ncst.transport.AbstractRpcServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 日拱一卒，不期速成
 *
 * @Auther: i
 * @Date: 2020/10/21/11:32
 * @Description: Netty服务器
 */
public class NettyServer extends AbstractRpcServer {

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);
    private CommonSerializer commonSerializer;

    public NettyServer (String host,int port) {
        this(host,port,DEFAULT_SERIAIIZER);
    }

    public NettyServer(String host, int port, int defaultSeriaiizer) {
        this.host = host;
        this.port = port;
        //服务提供者
        serviceProvider = new ServiceProviderImpl();
        //服务注册者
        serviceRegistry = new NacosServiceRegistry();
        //默认序列化方式
        this.commonSerializer = CommonSerializer.getBytes(defaultSeriaiizer);

        //扫描服务ScanService Scan
        scanServices();
    }

    @Override
    public void start() {
        //钩子 清除所有实例
        ShutDownHook.getInstance().clearAllService();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup,workGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))//log日志
                    //初始化可连接服务端队列大小，同一时间可以处理的客户端连接数
                    .option(ChannelOption.SO_BACKLOG,256)
                    //一直保持连接状态
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    //tcp不延时
                    .childOption(ChannelOption.TCP_NODELAY,true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            //IdleStsteHandler Netty提供的处理空闲状态的处理器
                            //readerIdleTime : 30S没有读 就发送一个心跳检测包看是否连接
                            //writerIdleTime : 0S没有写就发送一个心跳检测包看是否连接
                            //alldleTime : 0S 没有读写发送一个心跳检测包是否连接
                            pipeline.addLast(new IdleStateHandler(300,0,0, TimeUnit.SECONDS))
                                    //编码器
                                    .addLast(new CommonEncoder(commonSerializer))
                                    //解码器
                                    .addLast(new CommoDecoder())
                                    //过滤器
                                    .addLast(new NettyServerHandler());

                        }
                    });

            //关闭
            ChannelFuture future = bootstrap.bind(host,port).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            logger.error("【...服务器关闭时出现错误...】");
            e.printStackTrace();
        } finally {
            //释放资源
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

}
