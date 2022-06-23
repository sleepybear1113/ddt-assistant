package cn.xiejx.ddtassistant.base;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/06/23 13:21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TestConfig extends BaseConfig {
    private static final long serialVersionUID = 147484878511983878L;

    private String t;

    @Override
    @JSONField(serialize = false)
    public String getFileName() {
        return "test.json";
    }

    @Override
    public BaseConfig defaultConfig() {
        TestConfig testConfig = new TestConfig();
        testConfig.setT("111");
        return testConfig;
    }
}
