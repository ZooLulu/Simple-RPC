package top.elvis.rpc.netty.server;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.elvis.rpc.entity.RpcRequest;
import top.elvis.rpc.entity.RpcResponse;
import top.elvis.rpc.registry.DefaultServiceRegistry;
import top.elvis.rpc.registry.ServiceRegistry;
import top.elvis.rpc.socket.server.RequestHandler;

/**
 * 接收 RpcRequest，并且执行调用
 * @author oofelvis
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);
    private static RequestHandler requestHandler;
    private static ServiceRegistry serviceRegistry;

    static {
        requestHandler = new RequestHandler();
        serviceRegistry = new DefaultServiceRegistry();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
        try {
            logger.info("server received request: {}", msg);
            String interfaceName = msg.getInterfaceName();
            //获取服务接口
            Object service = serviceRegistry.getService(interfaceName);
            //调用服务接口
            Object result = requestHandler.handle(msg, service);
            //返回成功调用结果
            ChannelFuture future = ctx.writeAndFlush(RpcResponse.success(result));
            future.addListener(ChannelFutureListener.CLOSE);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("rpc invoke error:");
        cause.printStackTrace();
        ctx.close();
    }
}
