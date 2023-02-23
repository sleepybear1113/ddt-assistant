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

    @Scheduled(fixedRate = 3600 * 1000)
    public void collectInfo() {
        if (!GlobalVariable.INFO_COLLECT_DTO.isInit()) {
            return;
        }

        String addr = "https://sleepybear.cn/ddt-assistant-collect/info/collect";
        HttpHelper httpHelper = HttpHelper.makeDefaultTimeoutHttpHelper(addr, MethodEnum.METHOD_POST);
        String jsonString = Util.parseObjectToJsonString(GlobalVariable.INFO_COLLECT_DTO);
        httpHelper.setPostBody(jsonString, ContentType.APPLICATION_JSON);
        httpHelper.request();
        GlobalVariable.INFO_COLLECT_DTO.clearCount();
    }

    @Scheduled(fixedDelay = 5 * 60 * 1000)
    public void initInfoCollect() {
        log.info("init info collect!");
        GlobalVariable.INFO_COLLECT_DTO.init();
        collectInfo();
    }
}
