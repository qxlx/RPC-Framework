package com.ncst.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * 日拱一卒，不期速成
 *
 * @Auther: i
 * @Date: 2020/10/20/17:23
 * @Description: 负载策略
 */
public interface LoadBalancer {

    /****
     * 获取实例
     * @param list
     * @return
     */
    Instance selectForInstance (List<Instance>  list);

}
