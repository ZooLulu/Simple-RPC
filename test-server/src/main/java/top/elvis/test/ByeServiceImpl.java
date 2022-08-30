package top.elvis.test;

import top.elvis.rpc.annotation.Service;
import top.elvis.rpc.api.ByeService;

/**
 * @author oofelvis
 */
@Service
public class ByeServiceImpl implements ByeService {

    @Override
    public String bye(String name) {
        return "bye, " + name;
    }
}
