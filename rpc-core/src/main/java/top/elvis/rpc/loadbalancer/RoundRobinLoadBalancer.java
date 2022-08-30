package top.elvis.rpc.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * 转轮算法
 * @author oofelvis
 */
public class RoundRobinLoadBalancer implements LoadBalancer {
    //定位选到第几个
    private int index = 0;

    @Override
    public Instance select(List<Instance> instances) {
        if(index >= instances.size()) {
            index %= instances.size();
        }
        return instances.get(index++);
    }

}