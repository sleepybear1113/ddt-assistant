package cn.xiejx.ddtassistant.controller;

import cn.xiejx.ddtassistant.logic.SystemLogic;
import cn.xiejx.ddtassistant.utils.Util;
import cn.xiejx.ddtassistant.vo.MyString;
import org.springframework.web.bind.annotation.RequestBody;
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

    @RequestMapping("/system/getHost")
    public MyString getHost() {
        String s = "sss\n555";
//        return new MyString(s);
        return systemLogic.getHost();
    }

    @RequestMapping("/system/updateHost")
    public Boolean updateHost(@RequestBody String host) {
        return systemLogic.updateHost(host);
    }

    @RequestMapping("/system/testAdmin")
    public Boolean testAdmin() {
        return Util.testAdmin();
    }
}
