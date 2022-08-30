package top.elvis.rpc.provider;

/**
 * 保存和提供服务实例对象
 * @author oofelvis
 */
public interface ServiceProvider {
    /**
     * 注册一个服务
     * @params
     * service: 待注册的服务实体
     */
    <T> void addServiceProvider(T service, String serviceName);

    Object getServiceProvider(String serviceName);
}
