package top.elvis.rpc.api;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
/**
 * @author oofelvis
 */
@Data
@AllArgsConstructor
//没有无参数构造器序列化过程会报错
@NoArgsConstructor
public class HelloObject implements Serializable {
    private Integer id;
    private String message;
}
