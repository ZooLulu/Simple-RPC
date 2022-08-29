package top.elvis.test;

import top.elvis.rpc.api.HelloService;
import top.elvis.rpc.netty.server.NettyServer;
import top.elvis.rpc.registry.DefaultServiceRegistry;
import top.elvis.rpc.registry.ServiceRegistry;
import top.elvis.rpc.socket.server.SocketServer;

/**
 * 服务器测试: 创建rpcServer对象，并注册预先定义的rpc对外提供的接口方法
 * @author oofelvis
 */
public class TestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        //注册服务
        serviceRegistry.register(helloService);
        //启动netty服务端，开启12580端口监听
        NettyServer rpcServer = new NettyServer();
        rpcServer.start(12580);
    }
}
