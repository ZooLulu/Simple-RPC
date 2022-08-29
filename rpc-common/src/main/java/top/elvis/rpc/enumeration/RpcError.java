package top.elvis.rpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * RPC error枚举类型
 * @author oofelvis
 */
@AllArgsConstructor
@Getter
public enum RpcError {
    SERVICE_INVOCATION_FAILURE("service invocation failure"),
    SERVICE_NOT_FOUND("service not found"),
    SERVICE_NOT_IMPLEMENT_ANY_INTERFACE("service not implement interface");

    private final String message;
}
