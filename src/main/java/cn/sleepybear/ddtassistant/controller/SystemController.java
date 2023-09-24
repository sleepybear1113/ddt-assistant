package cn.sleepybear.ddtassistant.controller;

import cn.sleepybear.ddtassistant.logic.SystemLogic;
import cn.sleepybear.ddtassistant.utils.Util;
import cn.sleepybear.ddtassistant.vo.FileInfoVo;
import cn.sleepybear.ddtassistant.advice.ResultCode;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResultCode<String> getHost() {
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
    public ResultCode<String> getLastSomeRows(String filename, Integer n) {
        return ResultCode.buildResult(systemLogic.getLastSomeRows(filename, n));
    }
}
