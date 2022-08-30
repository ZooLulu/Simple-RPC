package top.elvis.rpc;

import top.elvis.rpc.serializer.CommonSerializer;

/**
 * 服务器通用接口
 * @author oofelvis
 */
public interface RpcServer {
    int DEFAULT_SERIALIZER = CommonSerializer.KRYO_SERIALIZER;
    void start();
    //用于向 Nacos 注册服务
    <T> void publishService(Object service, Class<T> serviceClass);
}
