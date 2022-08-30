package top.elvis.test;

import top.elvis.rpc.RpcServer;
import top.elvis.rpc.annotation.ServiceScan;
import top.elvis.rpc.api.HelloService;
import top.elvis.rpc.serializer.CommonSerializer;
import top.elvis.rpc.serializer.KryoSerializer;
import top.elvis.rpc.socket.server.SocketServer;

/**
 * @author oofelvis
 */
@ServiceScan
public class SocketTestServer {

    public static void main(String[] args) {
        RpcServer socketServer = new SocketServer("127.0.0.1", 12581, CommonSerializer.HESSIAN_SERIALIZER);
        socketServer.start();
    }

}