package com.ncst.serializer;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.ncst.rpc.enumeration.SerializerCode;
import com.ncst.rpc.exception.SerializeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 日拱一卒，不期速成
 *
 * @Auther: i
 * @Date: 2020/10/20/13:02
 * @Description: 基于Hession协议的序列化器
 */
public class HessianSerializer implements CommonSerializer{

    private static final Logger logger = LoggerFactory.getLogger(HessianSerializer.class);

    @Override
    public byte[] serialize(Object obj) {
        HessianOutput hessianOutput = null;
        //输出
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()){
            hessianOutput = new HessianOutput(baos);
            hessianOutput.writeObject(obj);
            return baos.toByteArray();
        } catch (Exception e) {
            logger.error("...hession序列化发生错误...");
            throw new SerializeException("...hession序列化发生错误...");
        } finally {
            if (hessianOutput != null) {
                try {
                    hessianOutput.close();
                } catch (IOException e) {
                    logger.error("...hession关闭流发生错误...");
                }
            }
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        HessianInput hessianOutput = null;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
            hessianOutput = new HessianInput(bais);
            return hessianOutput.readObject(clazz);
        } catch (Exception e) {
            logger.error("...hession反序列化发生错误...");
        } finally {
            if (hessianOutput != null) {
                try {
                    hessianOutput.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    public int getCode() {
        return SerializerCode.valueOf("HESSION").getCode();
    }
}
