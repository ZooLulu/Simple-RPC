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
import top.elvis.rpc.serializer.JsonSerializer;

/**
 * netty服务消费方
 * @author oofelvis
 */
public class NettyClient implements RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private String host;
    private int port;
    private static final Bootstrap bootstrap;

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
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
                .option(ChannelOption.SO_KEEPALIVE, true)
                //使用匿名内部类初始化通道
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        //给pipeline管道设置处理器：解码器、编码器、数据处理器
                        pipeline.addLast(new CommonDecoder())
                                .addLast(new CommonEncoder(new JsonSerializer()))
                                .addLast(new NettyClientHandler());
                    }
                });
    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        try {

            ChannelFuture future = bootstrap.connect(host, port).sync();
            logger.info("client connect to server {}:{}", host, port);
            Channel channel = future.channel();
            if(channel != null) {
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
                return rpcResponse.getData();
            }

        } catch (InterruptedException e) {
            logger.error("send message error: ", e);
        }
        return null;
    }
}
