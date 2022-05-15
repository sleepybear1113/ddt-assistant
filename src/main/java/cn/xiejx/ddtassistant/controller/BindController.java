package cn.xiejx.ddtassistant.controller;

import cn.xiejx.ddtassistant.logic.BindLogic;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author sleepybear
 */
@RestController
public class BindController {

    @Resource
    private BindLogic bindLogic;

}
