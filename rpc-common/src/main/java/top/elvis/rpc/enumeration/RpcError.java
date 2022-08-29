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
    SERVICE_NOT_IMPLEMENT_ANY_INTERFACE("service not implement interface"),
    UNKNOWN_PROTOCOL("unknown protocol"),
    UNKNOWN_SERIALIZER("unknown serializer"),
    UNKNOWN_PACKAGE_TYPE("unknown package type"),
    CLIENT_CONNECT_SERVER_FAILURE("client connect server failure"),
    SERIALIZER_NOT_FOUND("serializer not found"),
    RESPONSE_NOT_MATCH("response not match"),
    FAILED_TO_CONNECT_TO_SERVICE_REGISTRY("failed to connect to service registry"),
    REGISTER_SERVICE_FAILED("register service failed");

    private final String message;
}
