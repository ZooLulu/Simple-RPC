package top.elvis.rpc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 客户端请求对象
 * @author oofelvis
 */
@Data
@AllArgsConstructor
//没有无参数构造器序列化过程会报错
@NoArgsConstructor
public class RpcRequest implements Serializable{
    //接口名称
    private String interfaceName;
    //方法名称
    private String methodName;
    //参数和参数类型
    private Object[] parameters;
    private Class<?>[] paramTypes;
}
