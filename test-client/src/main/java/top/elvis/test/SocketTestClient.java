package top.elvis.test;

import top.elvis.rpc.api.ByeService;
import top.elvis.rpc.api.HelloObject;
import top.elvis.rpc.api.HelloService;
import top.elvis.rpc.serializer.CommonSerializer;
import top.elvis.rpc.serializer.KryoSerializer;
import top.elvis.rpc.socket.client.RpcClientProxy;
import top.elvis.rpc.socket.client.SocketClient;

/**
 * @author oofelvis
 */
public class SocketTestClient {
    public static void main(String[] args) {
        SocketClient client = new SocketClient(CommonSerializer.KRYO_SERIALIZER);
        RpcClientProxy proxy = new RpcClientProxy(client);
        HelloService helloService = proxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "This is a message");
        String res = helloService.hello(object);
        System.out.println(res);
        ByeService byeService = proxy.getProxy(ByeService.class);
        System.out.println(byeService.bye("Netty"));
    }
}
