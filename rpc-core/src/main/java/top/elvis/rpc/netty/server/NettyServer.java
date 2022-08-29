package top.elvis.rpc.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.elvis.rpc.RpcServer;
import top.elvis.rpc.codec.CommonDecoder;
import top.elvis.rpc.codec.CommonEncoder;
import top.elvis.rpc.enumeration.RpcError;
import top.elvis.rpc.exception.RpcException;
import top.elvis.rpc.provider.ServiceProvider;
import top.elvis.rpc.provider.ServiceProviderImpl;
import top.elvis.rpc.registry.NacosServiceRegistry;
import top.elvis.rpc.registry.ServiceRegistry;
import top.elvis.rpc.serializer.CommonSerializer;
import top.elvis.rpc.serializer.JsonSerializer;
import top.elvis.rpc.serializer.KryoSerializer;

import java.net.InetSocketAddress;

/**
 * netty服务提供方
 * @author oofelvis
 */
public class NettyServer implements RpcServer {
    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private final String host;
    private final int port;

    //注册中心
    private final ServiceRegistry serviceRegistry;
    //本地注册表
    private final ServiceProvider serviceProvider;
    //定义序列化工具
    private CommonSerializer serializer;

    public NettyServer(String host, int port) {
        this.host = host;
        this.port = port;
        serviceRegistry = new NacosServiceRegistry();
        serviceProvider = new ServiceProviderImpl();
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public <T> void publishService(Object service, Class<T> serviceClass) {
        //未设置序列化器
        if(serializer == null) {
            logger.error("serializer not set");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        //将服务保存在本地的注册表，同时注册到 Nacos 上
        serviceProvider.addServiceProvider(service);
        serviceRegistry.register(serviceClass.getCanonicalName(), new InetSocketAddress(host, port));
        start();
    }

    @Override
    public void start() {
        //创建两个线程组 boosGroup、workerGroup
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //创建服务端的启动对象，设置参数
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            //设置两个线程组boosGroup和workerGroup
            serverBootstrap.group(bossGroup, workerGroup)
                    //设置服务端通道实现类型NioServerSocketChannel: 异步非阻塞的服务器端 TCP Socket 连接
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    //设置线程队列得到连接个数
                    .option(ChannelOption.SO_BACKLOG, 256)
                    //设置保持活动连接状态
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    //使用匿名内部类的形式初始化通道对象
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            //给pipeline管道设置处理器：编码器、解码器、数据处理器
                            pipeline.addLast(new CommonEncoder(serializer));
                            pipeline.addLast(new CommonDecoder());
                            pipeline.addLast(new NettyServerHandler());
                        }
                    });
            //绑定端口号，启动服务端
            ChannelFuture future = serverBootstrap.bind(port).sync();
            //对关闭通道进行监听
            future.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            logger.error("Start server error: ", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
