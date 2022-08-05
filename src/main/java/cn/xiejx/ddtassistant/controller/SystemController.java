package cn.xiejx.ddtassistant.controller;

import cn.xiejx.ddtassistant.logic.SystemLogic;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/06/05 21:10
 */
@RestController
public class SystemController {
    @Resource
    private SystemLogic systemLogic;

    @RequestMapping("/system/openWithExplorer")
    public void openWithExplorer(String path, Boolean select) {
        systemLogic.openWithExplorer(path, Boolean.TRUE.equals(select));
    }
}
