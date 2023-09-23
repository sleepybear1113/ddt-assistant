package cn.sleepybear.ddtassistant.controller;

import cn.sleepybear.ddtassistant.logic.BindLogic;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author sleepybear
 */
@RestController
public class BindController {

    @Resource
    private BindLogic bindLogic;

}
