package top.elvis.rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import top.elvis.rpc.entity.RpcRequest;
import top.elvis.rpc.enumeration.PackageType;
import top.elvis.rpc.serializer.CommonSerializer;

/**
 * 通用的编码拦截器:继承了MessageToByteEncoder类, 把 Message（实际要发送的对象）转化成 Byte 数组
 * 协议内容:
 * Magic Number：表明一个协议包
 * Package Type: 表明这是一个调用请求还是调用响应
 * Serializer Type: 表明了实际数据使用的序列化器，这个服务端和客户端应当使用统一标准；
 * Data Length 就是实际数据的长度，设置这个字段主要防止粘包
 * +---------------+---------------+-----------------+-------------+
 * |  Magic Number |  Package Type | Serializer Type | Data Length |
 * |    4 bytes    |    4 bytes    |     4 bytes     |   4 bytes   |
 * +---------------+---------------+-----------------+-------------+
 * |                          Data Bytes                           |
 * |                   Length: ${Data Length}                      |
 * +---------------------------------------------------------------+
 * @author oofelvis
 */
public class CommonEncoder extends MessageToByteEncoder {
    //4 字节魔数，表识一个协议包
    private static final int MAGIC_NUMBER = 0xCAFEBABE;
    private final CommonSerializer serializer;

    public CommonEncoder(CommonSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        //Magic Number:4 bytes
        out.writeInt(MAGIC_NUMBER);
        //判段是RpcRequest还是RpcResponse
        //Package Type:4 bytes
        if(msg instanceof RpcRequest) {
            out.writeInt(PackageType.REQUEST_PACK.getCode());
        } else {
            out.writeInt(PackageType.RESPONSE_PACK.getCode());
        }
        //Serializer Type:4 bytes
        out.writeInt(serializer.getCode());
        byte[] bytes = serializer.serialize(msg);
        //Data Length: 4 bytes
        out.writeInt(bytes.length);
        //data bytes
        out.writeBytes(bytes);
    }
}
