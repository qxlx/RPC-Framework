# 循序渐进写RPC

## 1.Rpc-Api模块构建

![RPC框架思路](https://cn-guoziyang.github.io/My-RPC-Framework/img/RPC%E6%A1%86%E6%9E%B6%E6%80%9D%E8%B7%AF.jpeg)



首先我们分析一下Dubbo的原理，才可以进一步构建RPC。总体流程来说，基于一个公共接口。服务端这一方有具体的实现。启动注册中心，主要有ZK、Nacos、Rureka等。服务端将服务注册到注册中心，而客户端通过服务名从注册中心获取服务，通过注册中心的地址，实现远程调用服务端的服务的具体实现。当然其中还涉及到很多细节，我们主要从最基础的组件进一步构建。

原理很简单，但是实现值得商榷，例如**客户端怎么知道服务端的地址**？**客户端怎么告诉服务端我要调用的接口**？**客户端怎么传递参数**？**只有接口客户端怎么生成实现类**……等等等等。

好了，下面我们进行模块划分。首先应该有一个RPC-API 将公共的接口放入。

RPC-API : 公共接口

RPC-Common : 公共组件

RPC-Core  : 核心组件

RPC-Client  :  客户端

RPC-Server ：服务端

### Rpc-Api中组件编写

`HelloService`  接口调用

`DataObject` 传输数据封装

`ByeService` 结束接口调用

## 2.Rpc-common模块构建

`entity.RpcRequest` 对于Rpc调用来说，就是一个请求和响应的过程。因为在传输过程中，需要封装自定义的数据格式信息。因此，自定义，首先需要给一个请求一个随机的ID，这里采用的是UUID生成。服务名 可以将接口名作为服务名，方法作为方法调用、以及对应的方法参数类型、方法参数、因为在功能中，我们还要首先一个心跳机制，所以加一个心跳是否处于心跳。

`entity.RpcResponse`  响应的数据，我们可以定义一个ID,请求与响应的ID必须一致。以及返回的状态码，可以用枚举写。以及传输中数据。补充信息。

==================================================

`ennumeration.PackageType`  数据的传输格式 无非就是 请求 和 响应

`enumeration.ResponseCode` 响应状态码  成功200 失败 500 对应信息

`enumeration.RpcError` 异常错误

`enumeration.SerializerCode` 序列号编码 序列化方式

==================================================

`exception.RpcException` rpc调用异常

`SerializeException` 序列化异常

==================================================

`factory.SingleFactory` 单例工厂类

`factory.ThreadPoolFactory` 线程池工厂

=================================================

`util.NacosUtil` Nacos连接工具 

主要方法有注册服务，通过ip地址+服务名。根据服务名获取所有的服务。注销服务。每次启动的时候。先注销其之前的服务。程序停止，删除所有服务。

`util.ReflectUtil` 获取栈调用轨迹。

`util.RpcMessageChecker` 判断请求和响应是否正常。

## 3.Rpc-core模块构建

### 注解层

`annotation.Service` 注解

`annitation.@SacnService` 扫描注解

=================================================

### 编解码层

`codec.commoDecoder`  判断是否是规定的传输规范

`codec.commoEncoder`  编辑传输规范

编码器因为是通过通道所以需要继承MessageToByteEncoder，我们自定义一套传输规范。

```
+---------------+---------------+-----------------+-------------+
|  Magic Number |  Package Type | Serializer Type | Data Length |
|    4 bytes    |    4 bytes    |     4 bytes     |   4 bytes   |
+---------------+---------------+-----------------+-------------+
|                          Data Bytes                           |
|                   Length: ${Data Length}                      |
+---------------------------------------------------------------+
```

魔数:使用java版本咖啡。包类型 请求或者响应包  序列化方式类型 数据长度 以及数据。

=================================================

### 请求拦截层

`handler.RequestHandler`  根据请求参数，调用对应的服务，而最终通过动态代理实现。

=================================================

### 回调层

`hook.ShutDownHook`  当系统停止时，清除服务。线程池关闭。

=================================================

### 负载均衡层

`loadbalancer.LoadBalancer ` 负载策略规范 

`loadbalancer.RandomLoadBalancer `  随机策略

`loadbalancer.RoundRobinLoadBalancer` 轮询策略

=================================================

### 服务提供者

`provider.serviceProvider`  服务添加和查找规范

`provider.ServiceProviderImpl ` 根据服务名查找服务和添加服务。主要思路是用两个Map存储，一个存储记录的map，一个记录服务。

=================================================

### 服务注册与发现

`registry.NacosServiceDiscovery` 通过负载策略查找服务名具体提供实例

`registry.NacosServiceRegistry` 注册到Nacos中

`registry.ServiceDiscovery`  服务发现接口  通过服务名查找服务

`registry.ServiceRegistry` 服务注册接口 通过服务名和地址信息(ip+port)注册到Nacos中

=================================================

### 序列化

`serializer.CommonSerializer`   序列化接口

`serializer.HessianSerializer` Hessian方式

`serializer.JsonSerialilizer` Json格式

`serializer.KryoSerialilizer` Kryo格式

`serializer.ProtobufSerialilizer` Protobu格式

=================================================

### 核心

`transport.RpcServer` 服务端接口规范

`transport.RpcClient` 客户端接口规范

`AbstractRpcServer` 主要初始化的时候，扫描带有@Service  @ScanService的注解。并将包下的类进行创建对应的对象。

`RpcClientProxy` 动态创建代理类，通过方法invoke 生成对应的RcpRequest对象发送给服务端，通过异步回调。

`NettyServerHandler` 心跳检测 + 发送数据

`NettyServer`Netty服务端



### 服务端主要流程

1.从测试端拿到端口号和Ip地址，以及序列化方式(默认就是Kryo方式)，初始化的过程中，创建服务提供者和服务注册者。因为NettyServer继承AbstractRpcServer，并调用了`scanServices()` 。

2.`scanServices()` 根据从递归调用栈中拿到栈底的栈帧，也就是启动Main的类进行扫描。将带有@ScanService的注解类加载，对应的基础包，包下的@Service注解 反射生成。对应的接口为服务名。实例为服务。调用了服务提供者和服务注册者。因为是对于服务端来说，所以服务端提供了服务提供和服务注册功能。

3.`start()` 设置一下，如果程序终止之前，将服务全部删除。Netty创建流程。获取到管道。设置心跳参数，编码器，解码器。这里要说一下，编码器和解码器是相对的。当服务端接收到数据时，会采用解码器进行解析数据，之后，服务端发送数据会进行编码器，将数据进行编码。因为我们在程序中设置了传输格式，所以必须符合才可以，否则的话，会出现问题。除了编码器和解码器，加了一个过滤器，数据的拦截。首先检测是否是心跳，如果是心跳则直接返回。否则的话 是客户端的数据，通过`RequestHandler`过滤器进行处理。**实际上处理数据的就是通过RequestHandler来处理的**，从请求中拿到服务名，通过方法名和方法参数获取对应的方法，调用invoke处理。

如果通道正常，将RPCResponse响应给客户端。 如果长时间检测到心跳未回应。然后服务端关闭。

### 客户端主要流程

1.客户端通过序列化方式和随机负载均衡以及服务发现功能。完成初始化。

2.通过客户端生成代理类。生成对应的HelloService.class代理对象。因为对象是代理，所以只要通过方法调用就会触发invoke的请求。**而主要逻辑就是invoke方法**

3.**invoke()**,生成一个RpcRequest对象，心跳机制默认是false  如果请求Client属于NettyClient 通过异步回调机制，发送请求。通过响应拿到rpcResponse对象，

4.**sendRequest()**,通过服务名 拿到查找对应服务，将数据写入到通道中。

## 4.Nacos服务启动

startup.cmd -m standalone

http://127.0.0.1:8848/nacos/index.html





## 问题迭代

1.怎么实现的同步？你的调用为什么是同步的？ 

答： Netty是同步非阻塞的，我在项目中使用的netty进行网络传输。



2.客户端发请求的时候，封装请求之后通过tcp发送到客户端的吗？

答：是的

 

3.客户端和服务端都用的tcp？ server那边怎么处理的请求

答：都用的tcp， server那边先反序列化获得请求类，请求类RPCRequest里面包含需要调用的方法和参数，执行对应方法后会把结果封装成RPCResponse类返回给客户端



4.是在netty的线程里面直接调用了业务代码吗？

答：不是，netty传输的是序列化后的字节码。需要在服务端桩反序列化后执行业务代码。 
