package cn.sleepybear.ddtassistant.update.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2023/02/09 22:31
 */
@Data
public class DownloadFileInfoVo implements Serializable {
    @Serial
    private static final long serialVersionUID = -230524349186779802L;

    private Integer successCount = 0;
    private Integer failCount = 0;

    public void addSuccessCount() {
        successCount++;
    }

    public void addFailCount() {
        failCount++;
    }

    public void addCount(Boolean b) {
        if (Boolean.TRUE.equals(b)) {
            addSuccessCount();
        } else {
            addFailCount();
        }
    }
}
