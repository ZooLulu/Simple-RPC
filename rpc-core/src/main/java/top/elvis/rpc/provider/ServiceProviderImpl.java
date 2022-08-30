package top.elvis.rpc.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.elvis.rpc.enumeration.RpcError;
import top.elvis.rpc.exception.RpcException;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务器默认的注册表实现
 * @author oofelvis
 */
public class ServiceProviderImpl implements ServiceProvider {
    private static final Logger logger = LoggerFactory.getLogger(ServiceProviderImpl.class);
    //保存服务名-服务对象,static全局唯一
    private static final Map<String, Object> serviceMap = new ConcurrentHashMap<>();
    //保存已经注册的对象,static全局唯一
    private static final Set<String> registeredService = ConcurrentHashMap.newKeySet();

    @Override
    public <T> void addServiceProvider(T service, String serviceName) {
        //已经注册过无需重复注册
        if(registeredService.contains(serviceName)) return;
        registeredService.add(serviceName);
        serviceMap.put(serviceName, service);
        logger.info("interface: {} service: {}", service.getClass().getInterfaces(), serviceName);
    }

    @Override
    public Object getServiceProvider(String serviceName) {
        Object service = serviceMap.get(serviceName);
        //处理服务不存在异常
        if(service == null) {
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        return service;
    }
}
