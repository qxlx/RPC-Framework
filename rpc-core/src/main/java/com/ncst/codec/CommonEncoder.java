package com.ncst.codec;

import com.ncst.rpc.entity.RpcRequest;
import com.ncst.rpc.enumeration.PackageType;
import com.ncst.serializer.CommonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 日拱一卒，不期速成
 *
 * @Auther: i
 * @Date: 2020/10/20/20:40
 * @Description: 编码器 自定义规则
 * Magic Number     Package Type        Serializer Type     Data length
 * 4 bytes            4 bytes              4 bytes             4 bytes
 * <p>
 * 魔数+包类型+序列化类型+data
 * 继承MessageToByteEncoder转换成Byte数组，将请求或响应包装成响应包。
 */
public class CommonEncoder extends MessageToByteEncoder {

    //魔数
    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    private CommonSerializer commonSerializer;

    public CommonEncoder(CommonSerializer commonSerializer) {
        this.commonSerializer = commonSerializer;
    }

    /******
     * 因为编码器对于客户端和服务端都会调用，因此需要进行区分，也就是通过数据包的类型来判断request response
     * @param ctx
     * @param msg
     * @param byteBuf
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf byteBuf) throws Exception {
        byteBuf.writeInt(MAGIC_NUMBER);//魔数
        //request
        if (msg instanceof RpcRequest) {
            byteBuf.writeInt(PackageType.REQUEST_PACK.getCode());
            //response
        } else {
            byteBuf.writeInt(PackageType.RESPONSE_PACK.getCode());
        }
        //序列化方式
        byteBuf.writeInt(commonSerializer.getCode());
        byte[] bytes = commonSerializer.serialize(msg);//序列化
        byteBuf.writeInt(bytes.length);//length
        byteBuf.writeBytes(bytes);//data
    }
}
