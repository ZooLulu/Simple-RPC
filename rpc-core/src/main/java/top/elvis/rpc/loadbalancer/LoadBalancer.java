package top.elvis.rpc.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * 负载均衡接口
 * @author oofelvis
 */
public interface LoadBalancer {

    //select 方法用于从一系列 Instance 中选择一个
    Instance select(List<Instance> instances);

}
