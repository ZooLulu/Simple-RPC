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
import top.elvis.rpc.socket.server.RequestHandler;
import top.elvis.rpc.util.ThreadPoolFactory;

import java.util.concurrent.ExecutorService;

/**
 * 接收 RpcRequest，并且执行调用
 * @author oofelvis
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);
    private static RequestHandler requestHandler;
    private static final String THREAD_NAME_PREFIX = "netty-server-handler";
    private static final ExecutorService threadPool;

    static {
        requestHandler = new RequestHandler();
        threadPool = ThreadPoolFactory.createDefaultThreadPool(THREAD_NAME_PREFIX);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
        threadPool.execute(() -> {
            try {
                logger.info("server received request: {}", msg);
                Object result = requestHandler.handle(msg);
                ChannelFuture future = ctx.writeAndFlush(RpcResponse.success(result, msg.getRequestId()));
                future.addListener(ChannelFutureListener.CLOSE);
            } finally {
                ReferenceCountUtil.release(msg);
            }
        });
//        try {
//            logger.info("server received request: {}", msg);
//            String interfaceName = msg.getInterfaceName();
//            //获取服务接口
//            Object service = serviceRegistry.getService(interfaceName);
//            //调用服务接口
//            Object result = requestHandler.handle(msg, service);
//            //返回成功调用结果
//            ChannelFuture future = ctx.writeAndFlush(RpcResponse.success(result));
//            future.addListener(ChannelFutureListener.CLOSE);
//        } finally {
//            ReferenceCountUtil.release(msg);
//        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("rpc invoke error:");
        cause.printStackTrace();
        ctx.close();
    }
}
