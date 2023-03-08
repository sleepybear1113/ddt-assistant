package cn.xiejx.ddtassistant.base;

import cn.xiejx.ddtassistant.type.vip.VipCoinThingValue;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2023/03/05 23:20
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class VipCoinConfig extends BaseConfig{
    private static final long serialVersionUID = 7600007504607407716L;

    private List<VipCoinThingValue> vipCoinThingValueList;

    @Override
    public String getFileName() {
        return "VIP币配置文件";
    }

    @Override
    public BaseConfig defaultConfig() {
        return null;
    }

}
