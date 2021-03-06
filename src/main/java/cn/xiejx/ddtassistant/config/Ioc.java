package cn.xiejx.ddtassistant.config;

import cn.xiejx.ddtassistant.base.OfflineDetectionConfig;
import cn.xiejx.ddtassistant.base.SettingConfig;
import cn.xiejx.ddtassistant.base.UserConfig;
import cn.xiejx.ddtassistant.constant.GlobalVariable;
import cn.xiejx.ddtassistant.dm.DmDdt;
import cn.xiejx.ddtassistant.type.TypeConstants;
import cn.xiejx.ddtassistant.type.auction.AuctionData;
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
    public OfflineDetectionConfig initOfflineDetectionConfig() {
        return new OfflineDetectionConfig().load();
    }

    @Bean
    public AuctionData initAuctionData() {
        return AuctionData.load();
    }

    @Bean
    public SettingConfig initSettingConfig() {
        return new SettingConfig().load();
    }

    @Bean
    public DmDdt initDmDdt() {
        DmDdt dmDdt = DmDdt.createInstance(null);
        initTemplateImgMap();
        return dmDdt;
    }

    public static void initTemplateImgMap() {
        GlobalVariable.templateImgMap = TypeConstants.TemplatePrefix.getTemplateImgMap();
    }
}
