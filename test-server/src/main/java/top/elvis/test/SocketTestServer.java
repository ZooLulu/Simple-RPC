package top.elvis.test;

import top.elvis.rpc.api.HelloService;
import top.elvis.rpc.serializer.CommonSerializer;
import top.elvis.rpc.serializer.KryoSerializer;
import top.elvis.rpc.socket.server.SocketServer;

/**
 * @author oofelvis
 */
public class SocketTestServer {

    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl2();
        SocketServer socketServer = new SocketServer("127.0.0.1", 12581, CommonSerializer.HESSIAN_SERIALIZER);
        socketServer.publishService(helloService, HelloService.class);
    }

}