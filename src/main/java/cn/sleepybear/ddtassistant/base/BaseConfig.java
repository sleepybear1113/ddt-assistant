package cn.sleepybear.ddtassistant.base;

import cn.sleepybear.ddtassistant.constant.Constants;
import cn.sleepybear.ddtassistant.utils.Util;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.io.File;
import java.io.Serial;
import java.io.Serializable;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/06/23 02:20
 */
@Data
@Slf4j
public abstract class BaseConfig implements Serializable {
    @Serial
    private static final long serialVersionUID = -187966677251404825L;

    /**
     * config 文件名
     *
     * @return 文件名
     */
    @JsonIgnore
    public abstract String getFileName();

    @JsonIgnore
    public String getFilePath() {
        if (StringUtils.isBlank(getFileName())) {
            return null;
        }
        return Constants.CONFIG_DIR + getFileName();
    }

    /**
     * 默认配置
     *
     * @return BaseConfig
     */
    public abstract BaseConfig defaultConfig();

    public void update(BaseConfig baseConfig) {
        BeanUtils.copyProperties(baseConfig, this);
    }

    @SuppressWarnings("unchecked")
    public <A extends BaseConfig> A load() {
        A defaultConfig = (A) defaultConfig();
        if (getFilePath() == null) {
            return defaultConfig;
        }
        if (!new File(getFilePath()).exists()) {
            return defaultConfig;
        }
        String s = Util.readFile(getFilePath());
        if (s == null || s.isEmpty()) {
            return defaultConfig;
        }

        try {
            return (A) Util.parseJsonToObject(s, getClass());
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return defaultConfig;
        }
    }

    public void save() {
        Util.writeFile(this, getFilePath());
    }
}
