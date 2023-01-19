package cn.xiejx.ddtassistant.dto;

import lombok.Data;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2023/01/17 21:21
 */
@Data
public class AbnormalDetectionCountDto {

    private int disconnectCount = 0;
    private int tokenExpiredCount = 0;
    private int offsiteCount = 0;
    private int leaveGameCount = 0;
    private int whiteScreenCount = 0;

    public void addDisconnect() {
        disconnectCount++;
    }

    public void addOffsite() {
        offsiteCount++;
    }

    public void addTokenExpired() {
        tokenExpiredCount++;
    }

    public void addLeaveGame() {
        leaveGameCount++;
    }

    public void addWhiteScreen() {
        whiteScreenCount++;
    }

    public boolean empty() {
        return this.leaveGameCount == 0 && this.offsiteCount == 0 && this.tokenExpiredCount == 0 && this.disconnectCount == 0 && this.whiteScreenCount == 0;
    }

    @Override
    public String toString() {
        return ("游戏异常情况：{" +
                (disconnectCount > 0 ? ("掉线=" + disconnectCount + ", ") : "") +
                (tokenExpiredCount > 0 ? ("凭证过期=" + tokenExpiredCount + ", ") : "") +
                (offsiteCount > 0 ? ("异地登录=" + offsiteCount + ", ") : "") +
                (leaveGameCount > 0 ? ("离开页面弹窗=" + leaveGameCount + ", ") : "") +
                (whiteScreenCount > 0 ? ("游戏白屏=" + whiteScreenCount + ", ") : "") +
                "}").replace(",}", "}");
    }
}
