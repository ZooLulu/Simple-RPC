package top.elvis.rpc.serializer;

/**
 * 通用的序列化反序列化接口
 * @author oofelvis
 */
public interface CommonSerializer {
    //序列化
    byte[] serialize(Object obj);
    //反序列化
    Object deserialize(byte[] bytes, Class<?> clazz);

    int getCode();

    static CommonSerializer getByCode(int code) {
        switch (code) {
            case 0:
                return new KryoSerializer();
            case 1:
                return new JsonSerializer();
            default:
                return null;
        }
    }
}
