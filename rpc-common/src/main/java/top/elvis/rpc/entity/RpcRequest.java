package top.elvis.rpc.entity;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 客户端请求对象
 * @author oofelvis
 */
@Data
@Builder
public class RpcRequest implements Serializable{
    //接口名称
    private String interfaceName;
    //方法名称
    private String methodName;
    //参数和参数类型
    private Object[] parameters;
    private Class<?>[] paramTypes;
}
