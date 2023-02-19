package cn.xiejx.ddtassistant.controller;

import cn.xiejx.ddtassistant.logic.SystemLogic;
import cn.xiejx.ddtassistant.utils.Util;
import cn.xiejx.ddtassistant.vo.FileInfoVo;
import cn.xiejx.ddtassistant.vo.MyString;
import cn.xiejx.ddtassistant.vo.ResultCode;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

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

    @RequestMapping("/system/getAvailableIpAddr")
    public List<List<String>> getAvailableIpAddr() {
        return systemLogic.getAvailableIpAddr();
    }

    @RequestMapping("/system/getLocalAllFiles")
    public List<String> getLocalAllFiles(String path, Boolean excludePath) {
        return systemLogic.getLocalAllFiles(path, excludePath);
    }

    @RequestMapping("/system/getLocalFileWithDir")
    public List<FileInfoVo> getLocalFileWithDir(String dir) {
        return systemLogic.getLocalFileWithDir(dir);
    }

    @RequestMapping("/system/getLastSomeRows")
    public ResultCode getLastSomeRows(String filename, Integer n) {
        return ResultCode.buildString(systemLogic.getLastSomeRows(filename, n));
    }
}
