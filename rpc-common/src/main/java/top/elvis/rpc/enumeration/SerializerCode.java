package top.elvis.rpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 字节流中标识序列化和反序列化器
 * @author oofelvis
 */
@AllArgsConstructor
@Getter
public enum SerializerCode {
    //JSON序列化编号
    JSON(1);

    private final int code;
}
