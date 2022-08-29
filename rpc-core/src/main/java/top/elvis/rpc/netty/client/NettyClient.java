package top.elvis.rpc.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.elvis.rpc.RpcClient;
import top.elvis.rpc.codec.CommonDecoder;
import top.elvis.rpc.codec.CommonEncoder;
import top.elvis.rpc.entity.RpcRequest;
import top.elvis.rpc.entity.RpcResponse;
import top.elvis.rpc.enumeration.RpcError;
import top.elvis.rpc.exception.RpcException;
import top.elvis.rpc.registry.NacosServiceRegistry;
import top.elvis.rpc.registry.ServiceRegistry;
import top.elvis.rpc.serializer.CommonSerializer;
import top.elvis.rpc.serializer.JsonSerializer;
import top.elvis.rpc.serializer.KryoSerializer;
import top.elvis.rpc.util.RpcMessageChecker;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicReference;

/**
 * netty服务消费方
 * @author oofelvis
 */
public class NettyClient implements RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    //注册中心进行服务发现
    private final ServiceRegistry serviceRegistry;
    private static final Bootstrap bootstrap;
    //序列化工具
    private CommonSerializer serializer;


    public NettyClient() {
        this.serviceRegistry = new NacosServiceRegistry();
    }

    static {
        EventLoopGroup group = new NioEventLoopGroup();
        //创建bootstrap对象，配置参数
        bootstrap = new Bootstrap();
        //设置线程组
        bootstrap.group(group)
                //设置客户端的通道实现类型:异步非阻塞的客户端 TCP Socket 连接
                .channel(NioSocketChannel.class)
                //设置保持活动连接状态
                .option(ChannelOption.SO_KEEPALIVE, true);
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        //未设置序列化器
        if(serializer == null) {
            logger.error("serializer not set");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        //原子引用
        AtomicReference<Object> result = new AtomicReference<>(null);
        try {
            //根据rpcRequest的接口名称进行服务发现
            InetSocketAddress inetSocketAddress = serviceRegistry.lookupService(rpcRequest.getInterfaceName());
            //根据发现的服务初始化channel
            Channel channel = ChannelProvider.get(inetSocketAddress, serializer);
            if(channel.isActive()) {
                //channel 将 RpcRequest 对象写出，并且等待服务端返回的结果
                //发送是非阻塞的，所以发送后会立刻返回，而无法得到结果
                channel.writeAndFlush(rpcRequest).addListener(future1 -> {
                    if(future1.isSuccess()) {
                        logger.info(String.format("client send message: %s", rpcRequest.toString()));
                    } else {
                        logger.error("send message error: ", future1.cause());
                    }
                });
                channel.closeFuture().sync();
                //通过 AttributeKey 的方式阻塞获得返回结果
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
                //channel读取rcpResponse，NettyClientHandler处理写入
                RpcResponse rpcResponse = channel.attr(key).get();
                RpcMessageChecker.check(rpcRequest, rpcResponse);
                result.set(rpcResponse.getData());
            }else{
                //避免客户端不关闭
                System.exit(0);
            }

        } catch (InterruptedException e) {
            logger.error("send message error: ", e);
        }
        //没有结果则返回AtomicReference默认值null
        return result.get();
    }
}
