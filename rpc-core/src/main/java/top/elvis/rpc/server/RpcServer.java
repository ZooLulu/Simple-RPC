package top.elvis.rpc.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * RPC服务器实现，使用线程池
 * @author oofelvis
 */
public class RpcServer {
    //服务器线程池
    private final ExecutorService threadPool;
    private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);
    public RpcServer(){
        int corePoolSize = 6;
        int maxPoolSize = 60;
        long keepAliveTime = 60;
        ArrayBlockingQueue<Runnable> blockingDeque = new ArrayBlockingQueue<>(100);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        //创建线程池
        threadPool = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.SECONDS, blockingDeque, threadFactory);
    }
    //注册服务,这里简化实现，对外提供一个接口的调用服务
    public void register(Object service, int port){
        try(ServerSocket serverSocket = new ServerSocket(port)){
            logger.info("RPC Server service starting......");
            Socket socket;
            //循环监听
            while ((socket=serverSocket.accept())!=null){
                logger.info("RPC client connect, ip is: "+socket.getInetAddress());
                threadPool.execute(new RpcWorkThread(socket, service));
            }
        }catch(IOException e){
            logger.error("RPC connect error: ", e);
        }
    }

}
