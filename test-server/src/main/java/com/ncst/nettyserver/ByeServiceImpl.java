package com.ncst.nettyserver;

import com.ncst.annotation.Service;
import com.ncst.api.ByeService;

/**
 * 日拱一卒，不期速成
 *
 * @Auther: i
 * @Date: 2020/10/21/17:20
 * @Description:
 */
@Service
public class ByeServiceImpl implements ByeService {

    @Override
    public String bye(String name) {
        return "【 服务端：】 再见"+name;
    }
}
