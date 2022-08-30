package top.elvis.rpc.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.elvis.rpc.RpcClient;
import top.elvis.rpc.entity.RpcRequest;
import top.elvis.rpc.entity.RpcResponse;
import top.elvis.rpc.enumeration.RpcError;
import top.elvis.rpc.exception.RpcException;
import top.elvis.rpc.loadbalancer.LoadBalancer;
import top.elvis.rpc.loadbalancer.RandomLoadBalancer;
import top.elvis.rpc.registry.NacosServiceDiscovery;
import top.elvis.rpc.registry.ServiceDiscovery;
import top.elvis.rpc.serializer.CommonSerializer;
import top.elvis.rpc.util.SingletonFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

/**
 * netty服务消费方
 * @author oofelvis
 */
public class NettyClient implements RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);
    private static final EventLoopGroup group;
    private static final Bootstrap bootstrap;
    static {
        group = new NioEventLoopGroup();
        //创建bootstrap对象，配置参数
        bootstrap = new Bootstrap();
        //设置线程组
        bootstrap.group(group)
                //设置客户端的通道实现类型:异步非阻塞的客户端 TCP Socket 连接
                .channel(NioSocketChannel.class);
    }
    //注册中心进行服务发现
    private final ServiceDiscovery serviceDiscovery;
    private final CommonSerializer serializer;
    private final UnprocessedRequests unprocessedRequests;

    public NettyClient() {
        this(DEFAULT_SERIALIZER, new RandomLoadBalancer());
    }
    public NettyClient(LoadBalancer loadBalancer) {
        this(DEFAULT_SERIALIZER, loadBalancer);
    }
    public NettyClient(Integer serializer) {
        this(serializer, new RandomLoadBalancer());
    }
    public NettyClient(Integer serializer, LoadBalancer loadBalancer) {
        this.serviceDiscovery = new NacosServiceDiscovery(loadBalancer);
        this.serializer = CommonSerializer.getByCode(serializer);
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }

    @Override
    public CompletableFuture<RpcResponse> sendRequest(RpcRequest rpcRequest) {
        //未设置序列化器
        if(serializer == null) {
            logger.error("serializer not set");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        //异步调用
        CompletableFuture<RpcResponse> resultFuture = new CompletableFuture<>();
        try {
            //根据rpcRequest的接口名称进行服务发现
            InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest.getInterfaceName());
            //根据发现的服务初始化channel
            Channel channel = ChannelProvider.get(inetSocketAddress, serializer);
            if (!channel.isActive()) {
                group.shutdownGracefully();
                return null;
            }
            unprocessedRequests.put(rpcRequest.getRequestId(), resultFuture);
            //channel 将 RpcRequest 对象写出，并且等待服务端返回的结果
            channel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener) future1 -> {
                if (future1.isSuccess()) {
                    logger.info(String.format("client send message: %s", rpcRequest.toString()));
                } else {
                    future1.channel().close();
                    resultFuture.completeExceptionally(future1.cause());
                    logger.error("send message error: ", future1.cause());
                }
            });
        } catch (Exception e) {
            unprocessedRequests.remove(rpcRequest.getRequestId());
            logger.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
        return resultFuture;
    }
}
