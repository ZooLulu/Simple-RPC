package top.elvis.rpc.registry;

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
public class DefaultServiceRegistry implements ServiceRegistry{
    private static final Logger logger = LoggerFactory.getLogger(DefaultServiceRegistry.class);
    //保存服务名-服务对象
    private final Map<String, Object> serviceMap = new ConcurrentHashMap<>();
    //保存已经注册的对象
    private final Set<String> registeredService = ConcurrentHashMap.newKeySet();

    @Override
    public <T> void register(T service) {
        //采用这个对象实现的接口的完整类名作为服务名
        String serviceName = service.getClass().getCanonicalName();
        //已经注册过无需重复注册
        if(registeredService.contains(serviceName)) return;
        registeredService.add(serviceName);
        //获得服务对应的接口
        Class<?>[] interfaces = service.getClass().getInterfaces();
        //处理服务无对应接口异常
        if(interfaces.length==0){
            throw new RpcException(RpcError.SERVICE_NOT_IMPLEMENT_ANY_INTERFACE);
        }

        for(Class<?> i : interfaces) {
            serviceMap.put(i.getCanonicalName(), service);
        }
        logger.info("Interface: {} registered service: {}", interfaces, serviceName);

    }

    @Override
    public Object getService(String serviceName) {
        Object service = serviceMap.get(serviceName);
        //处理服务不存在异常
        if(service == null) {
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        return service;
    }
}
