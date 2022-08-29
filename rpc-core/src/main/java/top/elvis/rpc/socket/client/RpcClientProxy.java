package top.elvis.rpc.socket.client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.elvis.rpc.RpcClient;
import top.elvis.rpc.entity.RpcRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * RPC客户端动态代理机制
 * @author oofelvis
 */
public class RpcClientProxy implements InvocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(RpcClientProxy.class);

    //客户端对象接口
    private final RpcClient client;

    public RpcClientProxy(RpcClient client){
        this.client = client;
    }
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> tClass){
        return (T) Proxy.newProxyInstance(tClass.getClassLoader(), new Class<?>[]{tClass},this);
    }
    /**
     * @params
     * proxy:动态代理对象
     * method:动态调用的方法
     * args:调用方法的参数
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        logger.info("Invoke method: {}#{}", method.getDeclaringClass().getName(), method.getName());
        RpcRequest rpcRequest = new RpcRequest(method.getDeclaringClass().getName(),
                method.getName(), args, method.getParameterTypes());

        return client.sendRequest(rpcRequest);
    }
}
