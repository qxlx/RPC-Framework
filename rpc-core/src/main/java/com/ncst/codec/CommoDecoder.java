package com.ncst.codec;

import com.ncst.rpc.entity.RpcRequest;
import com.ncst.rpc.entity.RpcResponse;
import com.ncst.rpc.enumeration.PackageType;
import com.ncst.rpc.enumeration.RpcError;
import com.ncst.rpc.exception.RpcException;
import com.ncst.serializer.CommonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 日拱一卒，不期速成
 *
 * @Auther: i
 * @Date: 2020/10/20/21:00
 * @Description: 解码器
 *  ReplayingDecoder 继承了ByteToMessageDecoder
 *  判断参数
 */
public class CommoDecoder extends ReplayingDecoder {

    private static final Logger logger = LoggerFactory.getLogger(CommoDecoder.class);

    private int MAGIC_NUMBERS = 0xCAFEBABE;

    /*****
     * 编码处理
     * @param channelHandlerContext
     * @param byteBuf
     * @param list
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        //魔数版本
        int magicNumber = byteBuf.readInt();
        if (magicNumber != MAGIC_NUMBERS) {
            logger.error("...错误的魔数号码...");
            throw new RpcException(RpcError.UNKNOWN_PROTOCOL);
        }

        int packageType = byteBuf.readInt();
        Class<?> packageClass = null;
        //包类型
        if (packageType == PackageType.REQUEST_PACK.getCode()) {
            packageClass = RpcRequest.class;
        } else if (packageType == PackageType.RESPONSE_PACK.getCode()) {
            packageClass = RpcResponse.class;
        } else {
            logger.error("...响应与请求不符合...");
            throw new RpcException(RpcError.UNKNOWN_PACKAGE_TYPE);
        }
        //序列化类型
        int serializer = byteBuf.readInt();
        CommonSerializer commonSerializer = CommonSerializer.getBytes(serializer);
        if (commonSerializer == null) {
            logger.error("...序列化出现问题...");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        //数据
        int length = byteBuf.readInt();
        byte [] bytes = new byte[length];
        byteBuf.readBytes(bytes);
        Object obj = commonSerializer.deserialize(bytes, packageClass);
        list.add(obj);
    }
}
