package top.elvis.test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.elvis.rpc.annotation.Service;
import top.elvis.rpc.api.HelloService;
import top.elvis.rpc.api.HelloObject;

/**
 * @author oofelvis
 */
@Service
public class HelloServiceImpl implements HelloService {

    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);

    @Override
    public String hello(HelloObject object) {
        logger.info("get message：{}", object.getMessage());
        return "HelloServiceImpl service";
    }

}
