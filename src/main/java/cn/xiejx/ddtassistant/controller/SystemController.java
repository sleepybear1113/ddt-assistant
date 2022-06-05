package cn.xiejx.ddtassistant.controller;

import cn.xiejx.ddtassistant.logic.SystemLogic;
import cn.xiejx.ddtassistant.vo.MemoryUseVo;
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

    @RequestMapping("/system/getMemoryUse")
    public List<MemoryUseVo> getMemoryUse() {
        return systemLogic.getMemoryUse();
    }
}
