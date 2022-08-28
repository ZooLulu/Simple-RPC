package top.elvis.test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.elvis.rpc.api.HelloService;
import top.elvis.rpc.api.HelloObject;

/**
 * @author oofelvis
 */
public class HelloServiceImpl implements HelloService{
    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);
    @Override
    public String hello(HelloObject object) {
        logger.info("Get message: {}", object.getMessage());
        return "Return id=" + object.getId();
    }
}
