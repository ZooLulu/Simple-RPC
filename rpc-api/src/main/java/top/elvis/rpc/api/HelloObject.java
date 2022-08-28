package top.elvis.rpc.api;
import lombok.Data;
import lombok.AllArgsConstructor;

import java.io.Serializable;
/**
 * @author oofelvis
 */
@Data
@AllArgsConstructor
public class HelloObject implements Serializable {
    private Integer id;
    private String message;
}
