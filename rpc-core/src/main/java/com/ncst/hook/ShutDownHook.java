package com.ncst.hook;

import com.ncst.rpc.factory.ThreadPoolFactory;
import com.ncst.rpc.util.NacosUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日拱一卒，不期速成
 *
 * @Auther: i
 * @Date: 2020/10/20/18:00
 * @Description:
 */
public class ShutDownHook {

    private static final Logger logger = LoggerFactory.getLogger(ShutDownHook.class);

    private static final ShutDownHook shutDownHook = new ShutDownHook();

    private ShutDownHook () {

    }

    public static ShutDownHook getInstance () {
        return shutDownHook;
    }

    /****
     * 当系统关闭之前 调用 将所有注册的服务进行删除。否则下一次启动 客户端
     * 会调用只有名字而没有服务的实例，出现错误
     */
    public void clearAllService () {
        logger.info("...关闭所有服务...");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            NacosUtil.clearRegistry();
            ThreadPoolFactory.shutDownAll();
        }));
    }

}
