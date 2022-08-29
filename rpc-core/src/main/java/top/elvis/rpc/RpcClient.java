package top.elvis.rpc;

import top.elvis.rpc.entity.RpcRequest;

/**
 * 客户端通用接口
 * @author oofelvis
 */
public interface RpcClient {
    Object sendRequest(RpcRequest rpcRequest);
}
