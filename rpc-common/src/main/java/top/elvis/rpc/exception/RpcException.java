package top.elvis.rpc.exception;

import top.elvis.rpc.enumeration.RpcError;
/**
 * 自定义RPC调用异常
 * @author oofelvis
 */
public class RpcException extends RuntimeException{
    public RpcException(RpcError error, String detail) {
        super(error.getMessage() + ": " + detail);
    }
    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }
    public RpcException(RpcError error) {
        super(error.getMessage());
    }
}
