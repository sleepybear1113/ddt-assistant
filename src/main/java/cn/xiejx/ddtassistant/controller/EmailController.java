package cn.xiejx.ddtassistant.controller;

import cn.xiejx.ddtassistant.dto.EmailConfigDto;
import cn.xiejx.ddtassistant.logic.EmailLogic;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/06/23 01:44
 */
@RestController
public class EmailController {
    @Resource
    private EmailLogic emailLogic;

    @RequestMapping("/email/sendTestEmail")
    public Boolean sendTestEmail() {
        return emailLogic.sendTestEmail();
    }

    @RequestMapping("/email/sendEmailByRemote")
    public Boolean sendEmailByRemote(@RequestBody EmailConfigDto emailConfigDto) {
        return emailLogic.sendEmailByRemote(emailConfigDto);
    }
}
