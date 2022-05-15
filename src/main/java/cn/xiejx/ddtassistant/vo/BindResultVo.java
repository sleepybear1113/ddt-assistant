package cn.xiejx.ddtassistant.vo;

import lombok.Data;

/**
 * @author sleepybear
 */
@Data
public class BindResultVo {
    private Integer newAddCount;
    private Integer runningCount;

    public BindResultVo() {
        this.newAddCount = 0;
        this.runningCount = 0;
    }

    public void increaseNewAddCount() {
        this.newAddCount++;
    }

    public void increaseRunningCount() {
        this.runningCount++;
    }

    public String buildInfo() {
        return String.format("新增线程数：%d，维持运行线程数：%d", this.newAddCount, this.runningCount);
    }
}
