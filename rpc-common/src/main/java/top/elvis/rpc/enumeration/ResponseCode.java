package top.elvis.rpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 服务器响应状态码
 * @author oofelvis
 */
@AllArgsConstructor
@Getter
public enum ResponseCode {
    SUCCESS(200,"Invoke success"),
    FAIL(500,"Invoke fail"),
    METHOD_NOT_FOUND(501,"Method not found"),
    CLASS_NOT_FOUND(502,"Class not found");

    private final int code;
    private final String message;
}
