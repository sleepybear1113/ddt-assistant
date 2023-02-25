package cn.xiejx.ddtassistant.schedule;

import cn.xiejx.ddtassistant.constant.GlobalVariable;
import cn.xiejx.ddtassistant.utils.Util;
import cn.xiejx.ddtassistant.utils.http.HttpHelper;
import cn.xiejx.ddtassistant.utils.http.enumeration.MethodEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2023/02/19 23:37
 */
@Component
@Slf4j
public class InfoCollectSchedule {
    private int times = 0;

    @Scheduled(fixedDelay = 1000 * 3600)
    public void collectInfo() {
        if (!GlobalVariable.INFO_COLLECT_DTO.isInit()) {
            return;
        }
        GlobalVariable.INFO_COLLECT_DTO.refreshHwndNum();

        String addr = "https://sleepybear.cn/ddt-assistant-collect/info/collect";
        HttpHelper httpHelper = HttpHelper.makeDefaultTimeoutHttpHelper(addr, MethodEnum.METHOD_POST);
        String jsonString = Util.parseObjectToJsonString(GlobalVariable.INFO_COLLECT_DTO);
        httpHelper.setPostBody(jsonString, ContentType.APPLICATION_JSON);
        httpHelper.request();
        GlobalVariable.INFO_COLLECT_DTO.clearCount();
    }

    @Scheduled(fixedDelay = 1000 * 300)
    public void initInfoCollect() {
        if (times > 1) {
            return;
        }
        if (times == 0) {
            times++;
            return;
        }

        if (times == 1 && !GlobalVariable.INFO_COLLECT_DTO.isInit()) {
            log.info("init info collect!");
            GlobalVariable.INFO_COLLECT_DTO.init();
            collectInfo();
        }

        times++;
    }
}
