package com.ncst.serializer;

/**
 * 日拱一卒，不期速成
 *
 * @Auther: i
 * @Date: 2020/10/20/13:03
 * @Description: 基于protobuf协议的序列化器  todo
 */
public class ProtobufSerializer implements CommonSerializer {

    @Override
    public byte[] serialize(Object obj) {
        return new byte[0];
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        return null;
    }

    @Override
    public int getCode() {
        return 0;
    }
}
