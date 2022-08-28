package top.elvis.rpc.client;
import top.elvis.rpc.entity.RpcRequest;
import top.elvis.rpc.entity.RpcResponse;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * RPC客户端动态代理机制
 * @author oofelvis
 */
public class RpcClientProxy implements InvocationHandler {
    //服务器主机号
    private String host;
    //服务器端口号
    private int port;
    public RpcClientProxy(String host, int port){
        this.host = host;
        this.port = port;
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
        RpcRequest rpcRequest = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameters(args)
                .paramTypes(method.getParameterTypes())
                .build();
        RpcClient rpcClient = new RpcClient();
        return ((RpcResponse) rpcClient.sendRquest(rpcRequest, host, port)).getData();
    }
}
