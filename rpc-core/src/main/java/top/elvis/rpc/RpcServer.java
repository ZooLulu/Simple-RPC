package top.elvis.rpc;

import top.elvis.rpc.serializer.CommonSerializer;

/**
 * 服务器通用接口
 * @author oofelvis
 */
public interface RpcServer {
    void start();
    //传入想要的序列化工具
    void setSerializer(CommonSerializer serializer);
    //用于向 Nacos 注册服务
    <T> void publishService(Object service, Class<T> serviceClass);
}
