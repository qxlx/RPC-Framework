package com.ncst.transport.server;

import com.ncst.handler.RequestHandler;
import com.ncst.rpc.entity.RpcRequest;
import com.ncst.rpc.entity.RpcResponse;
import com.ncst.rpc.factory.SingletonFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日拱一卒，不期速成
 *
 * @Auther: i
 * @Date: 2020/10/21/14:48
 * @Description:  Netty中处理的RpcRequest的Handler
 *  因为SimpleChannelInboundHandler继承ChannelInboundHandlerAdapter
 *  就可以处理入站数据处理的应用容器
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);
    private RequestHandler requestHandler;


    public NettyServerHandler () {
        //生成一个单实例
        this.requestHandler = SingletonFactory.getInstance(RequestHandler.class);
    }

    /****
     * 读取客户端数据
     * @param ctx
     * @param rpcRequest
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest rpcRequest) throws Exception {
        try {
            if (rpcRequest.isHeartBeat()) {
                logger.info("【---接收到客户端心跳包 ---】");
                return;
            }

            System.out.println("【...服务器收到请求...】 "+rpcRequest.toString());

            //处理数据请求
            Object result = requestHandler.handle(rpcRequest);
            if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                ctx.writeAndFlush(RpcResponse.success(result,rpcRequest.getId()));
            } else  {
                logger.error("【...通道不可写...】");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // todo
            ReferenceCountUtil.release(rpcRequest);
        }

    }

    /*****
     * 异常处理
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("【...处理过程调用时有错误发生...】");
        cause.printStackTrace();
        ctx.close();
    }


    /****
     * 开启心跳机制后，如果连接后的时间太长，将会触发一个IdleStateEvent事件。
     * 重写userEventTriggered 可以处理该事件
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //心跳检测 如果属于IdelStateEvent事件
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            //未收到读事件
            if (state == IdleState.READER_IDLE) {
                logger.info("【...长时间未收到心跳包，断开连接...】");
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx,ctx);
        }
    }
}
