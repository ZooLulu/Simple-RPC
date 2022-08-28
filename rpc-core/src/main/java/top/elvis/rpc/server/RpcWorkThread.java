package top.elvis.rpc.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.elvis.rpc.entity.RpcRequest;
import top.elvis.rpc.entity.RpcResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

/**
 * RPC服务器实际工作线程，基于反射调用
 * 接收RpcRequest对象，解析并且调用，生成RpcResponse对象并传输回去
 * @author oofelvis
 */
public class RpcWorkThread implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(RpcWorkThread.class);
    private Socket socket;
    private Object service;

    public RpcWorkThread(Socket socket, Object service){
        this.socket = socket;
        this.service = service;
    }

    @Override
    public void run(){
        try(ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())){
            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
            //反射解析: 根据rpc客户端传输来的方法名和参数类型列表，解析确定具体方法
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(),rpcRequest.getParamTypes());
            //反射调用: 根据rpc客户端传输的参数执行具体的方法调用
            Object returnObject = method.invoke(service, rpcRequest.getParameters());
            objectOutputStream.writeObject(RpcResponse.success(returnObject));
            objectOutputStream.flush();
        }catch (IOException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException |
                InvocationTargetException e){
            logger.error("RPC server invoke or send error: ", e);
        }
    }
}
