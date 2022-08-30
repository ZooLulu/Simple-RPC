package top.elvis.rpc.socket.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.elvis.rpc.RpcClient;
import top.elvis.rpc.entity.RpcRequest;
import top.elvis.rpc.entity.RpcResponse;
import top.elvis.rpc.enumeration.ResponseCode;
import top.elvis.rpc.enumeration.RpcError;
import top.elvis.rpc.exception.RpcException;
import top.elvis.rpc.loadbalancer.LoadBalancer;
import top.elvis.rpc.loadbalancer.RandomLoadBalancer;
import top.elvis.rpc.registry.NacosServiceDiscovery;
import top.elvis.rpc.registry.NacosServiceRegistry;
import top.elvis.rpc.registry.ServiceDiscovery;
import top.elvis.rpc.registry.ServiceRegistry;
import top.elvis.rpc.serializer.CommonSerializer;
import top.elvis.rpc.socket.util.ObjectReader;
import top.elvis.rpc.socket.util.ObjectWriter;
import top.elvis.rpc.util.RpcMessageChecker;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Socket方式远程方法调用的消费者（客户端）
 * RPC客户端实现：发送request并得到response, 使用java io序列化传输对象
 * @author oofelvis
 */
public class SocketClient implements RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(RpcClient.class);
    //服务发现
    private final ServiceDiscovery serviceDiscovery;
    //定义序列化工具
    private CommonSerializer serializer;
    public SocketClient() {
        this(DEFAULT_SERIALIZER, new RandomLoadBalancer());
    }
    public SocketClient(LoadBalancer loadBalancer) {
        this(DEFAULT_SERIALIZER, loadBalancer);
    }
    public SocketClient(Integer serializer) {
        this(serializer, new RandomLoadBalancer());
    }

    public SocketClient(Integer serializer, LoadBalancer loadBalancer) {
        this.serviceDiscovery = new NacosServiceDiscovery(loadBalancer);
        this.serializer = CommonSerializer.getByCode(serializer);
    }
    @Override
    public Object sendRequest(RpcRequest rpcRequest){
        if(serializer == null) {
            logger.error("serializer not set");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        //服务发现
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest.getInterfaceName());
        try(Socket socket=new Socket()){
            socket.connect(inetSocketAddress);
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();
            ObjectWriter.writeObject(outputStream, rpcRequest, serializer);
            Object obj = ObjectReader.readObject(inputStream);
            RpcResponse rpcResponse = (RpcResponse) obj;
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
            RpcMessageChecker.check(rpcRequest, rpcResponse);
            return rpcResponse;
        }catch (IOException e){
            logger.error("sendRquest error: ", e);
            throw new RpcException("service invocation failure: ", e);
        }
    }
}
