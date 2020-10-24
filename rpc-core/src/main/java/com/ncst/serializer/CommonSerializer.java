package com.ncst.serializer;

/**
 * 日拱一卒，不期速成
 *
 * @Auther: i
 * @Date: 2020/10/19/18:13
 * @Description: 通用的序列化反序列化接口
 */
public interface CommonSerializer {

    Integer KRYO_SERIALIZER = 0;
    Integer JSON_SERIALIZER = 1;
    Integer HESSION_SERIALIZER = 2;
    Integer PROTOBUF_SERIALIZER = 3;

    Integer DEFAULT_SERIALIZER = KRYO_SERIALIZER;

    static CommonSerializer getBytes (int code) {
        switch (code) {
            case 0 :
                return new KryoSerializer();
            case 1:
                return new JsonSerializer();
            case 2:
                return new HessianSerializer();
            case 3:
                return new ProtobufSerializer();
            default:
                return null;
        }
    }

    /***
     * 序列化 将obj
     * @param obj
     * @return
     */
    byte [] serialize (Object obj);

    /****
     * 反序列化
     * @param bytes
     * @param clazz
     * @return
     */
    Object deserialize (byte [] bytes,Class<?> clazz);

    /****
     * 序列化器编号
     * @return
     */
    int getCode();

}
