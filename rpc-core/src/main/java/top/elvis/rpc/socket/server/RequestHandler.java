package top.elvis.rpc.socket.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.elvis.rpc.entity.RpcRequest;
import top.elvis.rpc.entity.RpcResponse;
import top.elvis.rpc.enumeration.ResponseCode;
import top.elvis.rpc.provider.ServiceProvider;
import top.elvis.rpc.provider.ServiceProviderImpl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 通过反射进行服务调用
 * @author oofelvis
 */
public class RequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    //全局唯一本地注册表
    private static final ServiceProvider serviceProvider;
    static {
        serviceProvider = new ServiceProviderImpl();
    }
    /**
     * 反射调用特定的方法
     * @params
     * rpcRequest: rpc请求
     * service： rpc服务接口
     * @return
     * Object: 执行结果
     */
    public Object handle(RpcRequest rpcRequest){
        Object service = serviceProvider.getServiceProvider(rpcRequest.getInterfaceName());
        return invokeTargetMethod(rpcRequest, service);
    }

    /**
     * 反射调用特定的方法
     * @params
     * rpcRequest: rpc请求
     * service： rpc服务接口
     * @return
     * Object: 执行结果
     */
    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service) {
        Object result;
        try {
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            result = method.invoke(service, rpcRequest.getParameters());
            logger.info("service:{} successful invoke method:{}", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            return RpcResponse.fail(ResponseCode.METHOD_NOT_FOUND, rpcRequest.getRequestId());
        }
        return result;
    }
}
