package cn.sleepybear.ddtassistant.controller;

import cn.sleepybear.ddtassistant.dto.EmailConfigDto;
import cn.sleepybear.ddtassistant.logic.EmailLogic;
import cn.sleepybear.ddtassistant.annotation.WithoutLogin;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @WithoutLogin
    @RequestMapping("/email/sendEmailByRemote")
    public Boolean sendEmailByRemote(@RequestBody EmailConfigDto emailConfigDto) {
        return emailLogic.sendEmailByRemote(emailConfigDto);
    }
}
