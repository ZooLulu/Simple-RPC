package top.elvis.rpc.socket.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.elvis.rpc.AbstractRpcServer;
import top.elvis.rpc.RpcServer;
import top.elvis.rpc.enumeration.RpcError;
import top.elvis.rpc.exception.RpcException;
import top.elvis.rpc.hook.ShutdownHook;
import top.elvis.rpc.provider.ServiceProvider;
import top.elvis.rpc.provider.ServiceProviderImpl;
import top.elvis.rpc.registry.NacosServiceRegistry;
import top.elvis.rpc.registry.ServiceRegistry;
import top.elvis.rpc.serializer.CommonSerializer;
import top.elvis.rpc.util.ThreadPoolFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * Socket方式远程方法调用的提供者（服务端）
 * RPC服务器实现，使用线程池, 利用服务注册表处理多个服务
 * @author oofelvis
 */
public class SocketServer extends AbstractRpcServer {
    //服务器线程池
    private final ExecutorService threadPool;
    private CommonSerializer serializer;
    //服务反射调用处理
    private RequestHandler requestHandler = new RequestHandler();

    public SocketServer(String host, int port) {
        this(host, port, DEFAULT_SERIALIZER);
    }

    public SocketServer(String host, int port, Integer serializer) {
        this.host = host;
        this.port = port;
        threadPool = ThreadPoolFactory.createDefaultThreadPool("socket-rpc-server");
        this.serviceRegistry = new NacosServiceRegistry();
        this.serviceProvider = new ServiceProviderImpl();
        this.serializer = CommonSerializer.getByCode(serializer);
        scanServices();
    }

    //监听请求，使用请求处理类和注册服务表进行后续处理
    public void start(){
        try(ServerSocket serverSocket = new ServerSocket()){
            serverSocket.bind(new InetSocketAddress(host, port));
            logger.info("RPC Server service starting......");
            ShutdownHook.getShutdownHook().addClearAllHook();
            Socket socket;
            //循环监听
            while ((socket=serverSocket.accept())!=null){
                logger.info("client connect: {}:{}", socket.getInetAddress(), socket.getPort());
                //开启一个服务处理线程
                threadPool.execute(new RequestHandlerThread(socket, requestHandler, serializer));
            }
            threadPool.shutdown();
        }catch(IOException e){
            logger.error("RPC connect error: ", e);
        }
    }

}
