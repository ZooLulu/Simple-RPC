package top.elvis.rpc.registry;

/**
 * 服务注册表接口
 * @author oofelvis
 */
public interface ServiceRegistry {
    /**
     * 注册一个服务
     * @params
     * service: 待注册的服务实体
     */
    <T> void register(T service);

    Object getService(String serviceName);
}
