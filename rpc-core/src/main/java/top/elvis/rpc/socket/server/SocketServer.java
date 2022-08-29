package top.elvis.rpc.socket.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.elvis.rpc.RpcServer;
import top.elvis.rpc.registry.ServiceRegistry;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * Socket方式远程方法调用的提供者（服务端）
 * RPC服务器实现，使用线程池, 利用服务注册表处理多个服务
 * @author oofelvis
 */
public class SocketServer implements RpcServer {
    //服务器线程池
    private final ExecutorService threadPool;
    //服务反射调用处理
    private RequestHandler requestHandler = new RequestHandler();
    //服务注册表
    private final ServiceRegistry serviceRegistry;
    private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);
    private static final int CORE_POOL_SIZE  = 6;
    private static final int MAXIMUM_POOL_SIZE = 60;
    private static final int KEEP_ALIVE_TIME = 60;
    private static final int BLOCKING_QUEUE_CAPACITY = 100;
    public SocketServer(ServiceRegistry serviceRegistry){
        this.serviceRegistry = serviceRegistry;
        ArrayBlockingQueue<Runnable> blockingDeque = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        //创建线程池
        threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE , MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, blockingDeque, threadFactory);
    }
    //监听请求，使用请求处理类和注册服务表进行后续处理
    public void start(int port){
        try(ServerSocket serverSocket = new ServerSocket(port)){
            logger.info("RPC Server service starting......");
            Socket socket;
            //循环监听
            while ((socket=serverSocket.accept())!=null){
                logger.info("RPC client connect, ip is: "+socket.getInetAddress());
                //开启一个服务处理线程
                threadPool.execute(new RequestHandlerThread(socket, requestHandler, serviceRegistry));
            }
            threadPool.shutdown();
        }catch(IOException e){
            logger.error("RPC connect error: ", e);
        }
    }

}
