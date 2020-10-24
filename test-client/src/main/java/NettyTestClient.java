import com.ncst.api.ByeService;
import com.ncst.api.DataObject;
import com.ncst.api.HelloService;
import com.ncst.serializer.CommonSerializer;
import com.ncst.transport.RpcClient;
import com.ncst.transport.RpcClientProxy;
import com.ncst.transport.client.NettyClient;

/**
 * 日拱一卒，不期速成
 *
 * @Auther: i
 * @Date: 2020/10/21/20:00
 * @Description:
 */
public class NettyTestClient {

    public static void main(String[] args) {
        //创建客户端+序列化
        RpcClient client = new NettyClient(CommonSerializer.DEFAULT_SERIALIZER);
        //代理
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        //生成代理类
        HelloService proxy = rpcClientProxy.getProxy(HelloService.class);
        //代理类调用
        String hello = proxy.hello(new DataObject(1,"【 客户端：】你好 "));

        System.out.println("【 服务端回响数据 】"+hello);

        ByeService bye = rpcClientProxy.getProxy(ByeService.class);

        System.out.println(bye.bye("【 客户端：】再见 "));
    }

}
