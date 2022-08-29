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
    //序列化工具编号
    KRYO(0),
    JSON(1);

    private final int code;
}
