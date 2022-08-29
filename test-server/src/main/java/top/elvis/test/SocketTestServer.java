package top.elvis.test;

import top.elvis.rpc.api.HelloService;
import top.elvis.rpc.serializer.KryoSerializer;
import top.elvis.rpc.socket.server.SocketServer;

/**
 * @author oofelvis
 */
public class SocketTestServer {

    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        SocketServer socketServer = new SocketServer("127.0.0.1", 12581);
        socketServer.setSerializer(new KryoSerializer());
        socketServer.publishService(helloService, HelloService.class);
    }

}