package top.elvis.test;

import top.elvis.rpc.api.HelloObject;
import top.elvis.rpc.api.HelloService;
import top.elvis.rpc.client.RpcClientProxy;

/**
 * RPC客户端测试，动态代理，生成代理对象，并且调用
 * @author oofelvis
 */
public class TestClient {
    public static void main(String[] args) {
        //动态代理
        RpcClientProxy proxy = new RpcClientProxy("127.0.0.1", 12580);
        HelloService helloService = proxy.getProxy(HelloService.class);
        //服务调用: 实际执行代理的invoke方法，发送rpcRquest，并得到rpcResponse
        HelloObject object = new HelloObject(666, "Test message");
        String res = helloService.hello(object);
        System.out.println(res);

    }
}
