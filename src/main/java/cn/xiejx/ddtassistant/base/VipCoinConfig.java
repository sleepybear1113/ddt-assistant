package cn.xiejx.ddtassistant.base;

import cn.xiejx.ddtassistant.type.vip.VipCoinThingValue;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2023/03/05 23:20
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class VipCoinConfig extends BaseConfig {
    private static final long serialVersionUID = 7600007504607407716L;

    private List<VipCoinOneConfig> vipCoinOneConfigList;

    @Override
    public String getFileName() {
        return "VIP币配置文件";
    }

    @Override
    public BaseConfig defaultConfig() {
        return null;
    }

    @Data
    public static class VipCoinOneConfig implements Serializable {
        private static final long serialVersionUID = 915694950137187709L;

        private String name;
        private List<VipCoinThingValue> vipCoinThingValueList;
    }

    @Data
    public static class VipCoinCondition implements Serializable {
        private static final long serialVersionUID = 4576903677653031081L;


    }

}
