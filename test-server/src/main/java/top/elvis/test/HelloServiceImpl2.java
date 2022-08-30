package top.elvis.test;

/**
 * @author oofelvis
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.elvis.rpc.api.HelloObject;
import top.elvis.rpc.api.HelloService;

public class HelloServiceImpl2 implements HelloService {

    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl2.class);

    @Override
    public String hello(HelloObject object) {
        logger.info("get message：{}", object.getMessage());
        return "Socket service";
    }
}
