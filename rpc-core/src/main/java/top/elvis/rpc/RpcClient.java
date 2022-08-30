package top.elvis.rpc;

import top.elvis.rpc.entity.RpcRequest;
import top.elvis.rpc.serializer.CommonSerializer;

/**
 * 客户端通用接口
 * @author oofelvis
 */
public interface RpcClient {
    int DEFAULT_SERIALIZER = CommonSerializer.KRYO_SERIALIZER;
    Object sendRequest(RpcRequest rpcRequest);

}
