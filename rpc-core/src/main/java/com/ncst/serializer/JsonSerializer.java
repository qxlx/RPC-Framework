package com.ncst.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ncst.rpc.entity.RpcRequest;
import com.ncst.rpc.enumeration.SerializerCode;
import com.ncst.rpc.exception.SerializeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 日拱一卒，不期速成
 *
 * @Auther: i
 * @Date: 2020/10/20/12:07
 * @Description: Json序列化
 */
public class JsonSerializer implements CommonSerializer {

    private static final Logger logger = LoggerFactory.getLogger(JsonSerializer.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(Object obj) {

        try {
            //obj序列化成字节数组
            return objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            logger.error("...json序列化发生错误...");
            throw new SerializeException("...序列化错误...");
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        Object obj = null;
        try {
            obj = objectMapper.readValue(bytes, clazz);
            if (obj instanceof RpcRequest) {
                //拦截一下
                obj = handlerRequest(obj);
            }
            return obj;
        } catch (IOException e) {
            logger.error("...json反序列化发生错误...");
            throw new SerializeException("..序列化错误...");
        }
    }

    @Override
    public int getCode() {
        return SerializerCode.valueOf("JSON").getCode();
    }

    //进行反序列化后无法还原真实对象 需要进行拦截处理  todo debug
    //总体思路 将obj进行转换一下 重新存储到rpcrequest 对象中
    private Object handlerRequest(Object obj) throws IOException {
        RpcRequest request = (RpcRequest) obj;
        for (int i = 0; i < request.getMethodTypes().length; i++) {
            //对应的类型
            Class<?> clazz = request.getMethodTypes()[i];
            if (!clazz.isAssignableFrom(request.getParam()[i].getClass())) {
                byte[] bytes = objectMapper.writeValueAsBytes(request.getParam()[i]);
                request.getParam()[i] = objectMapper.readValue(bytes, clazz);
            }
        }
        return request;
    }
}
