package cn.sleepybear.ddtassistant.config;

import cn.sleepybear.ddtassistant.base.*;
import cn.sleepybear.ddtassistant.dm.DmDdt;
import cn.sleepybear.ddtassistant.type.TypeConstants;
import cn.sleepybear.ddtassistant.type.auction.AuctionData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author sleepybear
 */
@Configuration
@Slf4j
public class Ioc {

    @Bean
    public UserConfig initUserConfig() {
        return new UserConfig().load();
    }

    @Bean
    public CaptchaConfig initCaptchaConfig() {
        CaptchaConfig captchaConfig = new CaptchaConfig().load();
        if (captchaConfig.getCaptchaInfoList() == null) {
            captchaConfig.setCaptchaInfoListPure(captchaConfig.defaultCaptchaList());
        }
        return captchaConfig;
    }

    @Bean
    public OfflineDetectionConfig initOfflineDetectionConfig() {
        return new OfflineDetectionConfig().load();
    }

    @Bean
    public AuctionData initAuctionData() {
        return AuctionData.load();
    }

    @Bean
    public SettingConfig initSettingConfig() {
        SettingConfig settingConfig = new SettingConfig().load();
        if (settingConfig.getUpdateConfig() == null) {
            settingConfig.setUpdateConfig(UpdateConfig.defaultConfig());
        }
        return settingConfig;
    }

    @Bean
    public DmDdt initDmDdt() {
        DmDdt dmDdt = DmDdt.createInstance(null);
        TypeConstants.TemplatePrefix.initTemplateImgMap();
        return dmDdt;
    }
}
