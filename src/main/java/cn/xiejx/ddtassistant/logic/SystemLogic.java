package cn.xiejx.ddtassistant.logic;

import cn.xiejx.ddtassistant.utils.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/06/05 21:13
 */
@Component
@Slf4j
public class SystemLogic {

    public void openWithExplorer(String path, boolean select) {
        Util.openWithExplorer(path, select);
    }

}
