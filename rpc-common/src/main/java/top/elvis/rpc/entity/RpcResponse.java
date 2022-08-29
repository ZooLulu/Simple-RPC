package top.elvis.rpc.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import top.elvis.rpc.enumeration.ResponseCode;

import java.io.Serializable;

/**
 * 服务器响应对象
 * @author oofelvis
 */
@Data
//没有无参数构造器序列化过程会报错
@NoArgsConstructor
public class RpcResponse<T> implements Serializable {
    //响应对应的请求号
    private String requestId;
    //响应状态码
    private Integer statusCode;
    //响应补充说明信息
    private String message;
    //响应数据
    private T data;

    //快速生成成功和失败响应对象
    public static <T> RpcResponse<T> success(T data, String requestId){
        RpcResponse<T> response = new RpcResponse<>();
        response.setRequestId(requestId);
        response.setStatusCode(ResponseCode.SUCCESS.getCode());
//        response.setMessage(ResponseCode.SUCCESS.getMessage());
        response.setData(data);
        return response;
    }
    public static <T> RpcResponse<T> fail(ResponseCode code, String requestId){
        RpcResponse<T> response = new RpcResponse<>();
        response.setRequestId(requestId);
        response.setStatusCode(code.getCode());
        response.setMessage(code.getMessage());
        return response;
    }
}
