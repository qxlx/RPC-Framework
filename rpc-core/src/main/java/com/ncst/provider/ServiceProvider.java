package com.ncst.provider;

/**
 * 日拱一卒，不期速成
 *
 * @Auther: i
 * @Date: 2020/10/20/16:52
 * @Description: 添加服务实例者
 */
public interface ServiceProvider {

    /****
     * 添加服务实例
     * @param service
     * @param serviceName
     * @param <T>
     */
    <T> void addServiceProvider(T service, String serviceName);


    /***
     * 根据服务实例获取服务
     * @param serviceName
     * @return
     */
    Object getServiceProvider(String serviceName);

}
