package com.ncst.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * 日拱一卒，不期速成
 *
 * @Auther: i
 * @Date: 2020/10/20/17:27
 * @Description: 轮询策略
 */
public class RoundRobinLoadBalancer implements LoadBalancer{

    private int index = 0;

    @Override
    public Instance selectForInstance(List<Instance> list) {
        System.out.println(list);
        if (index >= list.size()) {
            index %= list.size() ;
        }
        return list.get(index++);
    }
}
