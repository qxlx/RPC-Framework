package com.ncst.nettyserver;

import com.ncst.annotation.Service;
import com.ncst.api.DataObject;
import com.ncst.api.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日拱一卒，不期速成
 *
 * @Auther: i
 * @Date: 2020/10/21/17:21
 * @Description:
 */
@Service
public class HelloServiceImpl implements HelloService {

    private static final Logger logger = LoggerFactory.getLogger(HelloService.class);

    @Override
    public String hello(DataObject object) {
        System.out.println("【...接收到消息...】"+object.getMessage());
        return "...服务端回应...你好...";
    }
}
