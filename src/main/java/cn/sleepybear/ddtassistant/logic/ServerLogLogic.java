package cn.sleepybear.ddtassistant.logic;

import cn.sleepybear.ddtassistant.utils.Util;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2023/02/11 23:19
 */
@Component
public class ServerLogLogic {
    public String getLastSomeRows(String filename, Integer n) {
        if (n == null || n <= 0) {
            n = 100;
        }
        return Util.readLastSomeRows(new File(filename), n);
    }
}
