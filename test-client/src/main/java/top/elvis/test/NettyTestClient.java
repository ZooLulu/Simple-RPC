package top.elvis.test;

import top.elvis.rpc.RpcClient;
import top.elvis.rpc.api.HelloObject;
import top.elvis.rpc.api.HelloService;
import top.elvis.rpc.netty.client.NettyClient;
import top.elvis.rpc.serializer.ProtobufSerializer;
import top.elvis.rpc.socket.client.RpcClientProxy;

/**
 * 测试用Netty消费者
 * @author oofelvis
 */
public class NettyTestClient {
    public static void main(String[] args) {
        RpcClient client = new NettyClient();
        client.setSerializer(new ProtobufSerializer());
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(666, "This is a message");
        String res = helloService.hello(object);
        System.out.println(res);

    }
}
