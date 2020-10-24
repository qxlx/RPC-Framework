package com.ncst.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;
import java.util.Random;

/**
 * 日拱一卒，不期速成
 *
 * @Auther: i
 * @Date: 2020/10/20/17:25
 * @Description: 随机策略
 */
public class RandomLoadBalancer implements LoadBalancer {

    @Override
    public Instance selectForInstance(List<Instance> list) {
        return list.get(new Random().nextInt(list.size()));
    }
}
