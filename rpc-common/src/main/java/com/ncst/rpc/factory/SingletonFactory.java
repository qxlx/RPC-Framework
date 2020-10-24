package com.ncst.rpc.factory;

import com.ncst.rpc.entity.RpcRequest;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 日拱一卒，不期速成
 *
 * @Auther: i
 * @Date: 2020/10/19/17:11
 * @Description: 工厂类
 */
public class SingletonFactory {

    private static Map<Class,Object> objectMap = new ConcurrentHashMap<>();

    private SingletonFactory () {}

    /***
     *  根据Class获取某一个对象
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T>  T getInstance (Class <T> clazz) {
        Object instance = objectMap.get(clazz);
        synchronized (clazz) {
            if (instance == null) {
                try {
                    instance = clazz.newInstance();
                    objectMap.put(clazz,instance);
                } catch (InstantiationException  | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return clazz.cast(instance);
    }


    public static void main(String[] args) {
        RpcRequest instance = getInstance(RpcRequest.class);
        System.out.println(instance);
    }

}

