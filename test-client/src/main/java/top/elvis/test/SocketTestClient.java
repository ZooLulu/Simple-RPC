package top.elvis.test;

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
        HelloObject object = new HelloObject(666, "This is a message");
        for(int i = 0; i < 20; i ++) {
            String res = helloService.hello(object);
            System.out.println(res);
        }
    }
}
