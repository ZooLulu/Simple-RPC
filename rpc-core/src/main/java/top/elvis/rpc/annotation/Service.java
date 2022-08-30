package top.elvis.rpc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 放在一个类上，标识这个类提供一个服务
 * 用于远程接口的实现类
 * @author oofelvis
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Service {
    //该服务的名称，默认值是该类的完整类名
    public String name() default "";

}
