package top.elvis.rpc.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.elvis.rpc.entity.RpcRequest;

import javax.imageio.IIOException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * RPC客户端实现：发送request并得到response, 使用java io序列化传输对象
 * @author oofelvis
 */
public class RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(RpcClient.class);
    public Object sendRquest(RpcRequest rpcRequest, String host, int port){
        try(Socket socket=new Socket(host,port)){
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream.writeObject(rpcRequest);
            objectOutputStream.flush();
            return objectInputStream.readObject();
        }catch (IOException | ClassNotFoundException e){
            logger.error("sendRquest error: ", e);
            return null;
        }
    }
}
