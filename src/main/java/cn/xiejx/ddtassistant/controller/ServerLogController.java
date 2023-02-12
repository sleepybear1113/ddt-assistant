package cn.xiejx.ddtassistant.controller;

import cn.xiejx.ddtassistant.logic.ServerLogLogic;
import cn.xiejx.ddtassistant.vo.ResultCode;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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
    public ResultCode getLastSomeRows(String filename, Integer n) {
        return ResultCode.buildString(serverLogLogic.getLastSomeRows(filename, n));
    }
}
