package top.elvis.test;

import top.elvis.rpc.api.HelloService;
import top.elvis.rpc.server.RpcServer;

/**
 * 服务器测试: 创建rpcServer对象，并注册预先定义的rpc对外提供的接口方法
 * @author oofelvis
 */
public class TestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        RpcServer rpcServer = new RpcServer();
        //注册服务，开启12580端口监听
        rpcServer.register(helloService, 12580);
    }
}
