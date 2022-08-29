package top.elvis.rpc.socket.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.elvis.rpc.RpcClient;
import top.elvis.rpc.entity.RpcRequest;
import top.elvis.rpc.entity.RpcResponse;
import top.elvis.rpc.enumeration.ResponseCode;
import top.elvis.rpc.enumeration.RpcError;
import top.elvis.rpc.exception.RpcException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Socket方式远程方法调用的消费者（客户端）
 * RPC客户端实现：发送request并得到response, 使用java io序列化传输对象
 * @author oofelvis
 */
public class SocketClient implements RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(RpcClient.class);
    private final String host;
    private final int port;

    public SocketClient(String host, int port) {
        this.host = host;
        this.port = port;
    }
    @Override
    public Object sendRequest(RpcRequest rpcRequest){
        try(Socket socket=new Socket(host,port)){
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream.writeObject(rpcRequest);
            objectOutputStream.flush();
            RpcResponse rpcResponse = (RpcResponse) objectInputStream.readObject();
            //无响应
            if(rpcResponse == null) {
                logger.error("service invocation failure, service: {}", rpcRequest.getInterfaceName());
                throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, " service:" + rpcRequest.getInterfaceName());
            }
            //调用失败
            if(rpcResponse.getStatusCode() == null || rpcResponse.getStatusCode() != ResponseCode.SUCCESS.getCode()) {
                logger.error("service invocation failure, service: {}, response:{}", rpcRequest.getInterfaceName(), rpcResponse);
                throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, " service:" + rpcRequest.getInterfaceName());
            }
            return rpcResponse.getData();
        }catch (IOException | ClassNotFoundException e){
            logger.error("sendRquest error: ", e);
            throw new RpcException("service invocation failure: ", e);
        }
    }
}
