package top.elvis.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.elvis.rpc.annotation.Service;
import top.elvis.rpc.annotation.ServiceScan;
import top.elvis.rpc.enumeration.RpcError;
import top.elvis.rpc.exception.RpcException;
import top.elvis.rpc.provider.ServiceProvider;
import top.elvis.rpc.registry.ServiceRegistry;
import top.elvis.rpc.util.ReflectUtil;

import java.net.InetSocketAddress;
import java.util.Set;

/**
 * @author oofelvis
 */
public abstract class AbstractRpcServer implements RpcServer {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected String host;
    protected int port;

    protected ServiceRegistry serviceRegistry;
    protected ServiceProvider serviceProvider;

    public void scanServices() {
        //返回栈底的main方法
        String mainClassName = ReflectUtil.getStackTrace();
        Class<?> startClass;
        try {
            //获取 main 所在的类
            startClass = Class.forName(mainClassName);
            //判断是否又注解值
            if(!startClass.isAnnotationPresent(ServiceScan.class)) {
                logger.error("class need @ServiceScan 注解");
                throw new RpcException(RpcError.SERVICE_SCAN_PACKAGE_NOT_FOUND);
            }
        } catch (ClassNotFoundException e) {
            logger.error("UNKNOWN_ERROR");
            throw new RpcException(RpcError.UNKNOWN_ERROR);
        }
        //获取注解的值
        String basePackage = startClass.getAnnotation(ServiceScan.class).value();
        if("".equals(basePackage)) {
            basePackage = mainClassName.substring(0, mainClassName.lastIndexOf("."));
        }
        //获取到所有的Class，逐个判断是否有Service注解，如果有的话，通过反射创建该对象，并且调用publishService注册即可
        Set<Class<?>> classSet = ReflectUtil.getClasses(basePackage);
        for(Class<?> clazz : classSet) {
            if(clazz.isAnnotationPresent(Service.class)) {
                String serviceName = clazz.getAnnotation(Service.class).name();
                Object obj;
                try {
                    obj = clazz.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    logger.error("创建 " + clazz + " 时有错误发生");
                    continue;
                }
                if("".equals(serviceName)) {
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for (Class<?> oneInterface: interfaces){
                        publishService(obj, oneInterface.getCanonicalName());
                    }
                } else {
                    publishService(obj, serviceName);
                }
            }
        }
    }

    @Override
    public <T> void publishService(T service, String serviceName) {
        serviceProvider.addServiceProvider(service, serviceName);
        serviceRegistry.register(serviceName, new InetSocketAddress(host, port));
    }

}
