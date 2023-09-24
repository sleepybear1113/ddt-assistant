package cn.sleepybear.ddtassistant.controller;

import cn.sleepybear.ddtassistant.logic.ServerLogLogic;
import cn.sleepybear.ddtassistant.advice.ResultCode;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2023/02/11 23:18
 */
@RestController
public class ServerLogController {
    @Resource
    private ServerLogLogic serverLogLogic;

    @RequestMapping("/serverLog/getLastSomeRows")
    public ResultCode<String> getLastSomeRows(String filename, Integer n) {
        return ResultCode.buildResult(serverLogLogic.getLastSomeRows(filename, n));
    }
}
