package top.elvis.rpc.socket.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.elvis.rpc.entity.RpcRequest;
import top.elvis.rpc.entity.RpcResponse;
import top.elvis.rpc.registry.ServiceRegistry;
import top.elvis.rpc.serializer.CommonSerializer;
import top.elvis.rpc.socket.util.ObjectReader;
import top.elvis.rpc.socket.util.ObjectWriter;

import java.io.*;
import java.net.Socket;

/**
 * rpc request 处理线程
 * @author oofelvis
 */
public class RequestHandlerThread implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandlerThread.class);
    private Socket socket;
    private RequestHandler requestHandler;
    private ServiceRegistry serviceRegistry;
    private CommonSerializer serializer;

    public RequestHandlerThread(Socket socket, RequestHandler requestHandler, ServiceRegistry serviceRegistry, CommonSerializer serializer) {
        this.socket = socket;
        this.requestHandler = requestHandler;
        this.serviceRegistry = serviceRegistry;
        this.serializer = serializer;
    }

    @Override
    public void run() {
        try (InputStream inputStream = socket.getInputStream();
             OutputStream outputStream = socket.getOutputStream()) {
            //获取服务接口
            RpcRequest rpcRequest = (RpcRequest) ObjectReader.readObject(inputStream);
//            String interfaceName = rpcRequest.getInterfaceName();
            //调用服务接口
            Object result = requestHandler.handle(rpcRequest);
            //返回成功调用结果
            RpcResponse<Object> response = RpcResponse.success(result, rpcRequest.getRequestId());
            ObjectWriter.writeObject(outputStream, response, serializer);
        } catch (IOException e) {
            logger.error("invoke or send error: ", e);
        }
    }
}
