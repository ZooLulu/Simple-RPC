package top.elvis.rpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 调用请求类型和调用响应类型
 * @author oofelvis
 */
@AllArgsConstructor
@Getter
public enum PackageType {

    REQUEST_PACK(0),
    RESPONSE_PACK(1);

    private final int code;

}
