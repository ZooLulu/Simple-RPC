package top.elvis.test;

import top.elvis.rpc.RpcServer;
import top.elvis.rpc.annotation.ServiceScan;
import top.elvis.rpc.api.HelloService;
import top.elvis.rpc.netty.server.NettyServer;
import top.elvis.rpc.serializer.CommonSerializer;
import top.elvis.rpc.serializer.ProtobufSerializer;

/**
 * 测试用Netty服务提供者
 * @author oofelvis
 */
@ServiceScan
public class NettyTestServer {

    public static void main(String[] args) {
        RpcServer server = new NettyServer("127.0.0.1", 12580, CommonSerializer.PROTOBUF_SERIALIZER);
        server.start();
    }

}