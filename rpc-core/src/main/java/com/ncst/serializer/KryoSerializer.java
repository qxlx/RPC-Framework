package com.ncst.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.ncst.rpc.entity.RpcRequest;
import com.ncst.rpc.entity.RpcResponse;
import com.ncst.rpc.enumeration.SerializerCode;
import com.ncst.rpc.exception.SerializeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * 日拱一卒，不期速成
 *
 * @Auther: i
 * @Date: 2020/10/20/12:32
 * @Description: Kryo序列化
 */
public class KryoSerializer implements CommonSerializer {

    private static final Logger logger = LoggerFactory.getLogger(KryoSerializer.class);

    //因为Kryo存在线程安全问题 需要存放到ThreadLocal中，需要的时候获取 不使用的时候remove;
    private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RpcRequest.class);
        kryo.register(RpcResponse.class);
        kryo.setReferences(true);
        kryo.setRegistrationRequired(false);
        return kryo;
    });

    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             Output output = new Output(byteArrayOutputStream)) {
            Kryo kryo = kryoThreadLocal.get();
            //将obj对象存储到kryo中
            kryo.writeObject(output, obj);
            kryoThreadLocal.remove();
            //为什么删除 具体看threadlocal细节
            return output.toBytes();
        } catch (Exception e) {
            logger.error("...Kryo序列化时出现错误...");
            throw new SerializeException("...Kryo序列化时出现错误...");
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        //反序列化操作
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             Input input = new Input(bis);) {
            Kryo kryo = kryoThreadLocal.get();
            Object o = kryo.readObject(input, clazz);
            kryoThreadLocal.remove();
            return o;
        } catch (Exception e) {
            logger.error("...Kryo反序列化时出现错误...");
            throw new SerializeException("...Kryo反序列化时出现错误...");
        }
    }

    @Override
    public int getCode() {
        return SerializerCode.valueOf("KRYO").getCode();
    }
}
