package cn.xiejx.ddtassistant.logic;

import cn.xiejx.ddtassistant.base.OfflineDetectionConfig;
import cn.xiejx.ddtassistant.constant.Constants;
import cn.xiejx.ddtassistant.constant.GlobalVariable;
import cn.xiejx.ddtassistant.dm.DmConstants;
import cn.xiejx.ddtassistant.dm.DmDdt;
import cn.xiejx.ddtassistant.dto.AbnormalDetectionCountDto;
import cn.xiejx.ddtassistant.dto.AbnormalDetectionDto;
import cn.xiejx.ddtassistant.exception.FrontException;
import cn.xiejx.ddtassistant.utils.ImgUtil;
import cn.xiejx.ddtassistant.utils.Util;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/06/23 02:19
 */
@Component
public class OfflineDetectionLogic {
    public static final int[] WHITE_SCREEN_COLOR = {255, 255, 255};
    public static final int[] WHITE_SCREEN_COLOR_DELTA = {3, 3, 3};
    public static final double MAJOR_WHITE_SCREEN_THRESHOLD = 0.98;

    public static final String OFFLINE_DIALOG_MSG = "对不起，断线了请刷新页面，重新登录！";
    public static final String OFFSITE_LOGIN_MSG = "您的帐号在别处登陆...";
    public static final String OFFSITE_LOGOUT_MSG = "您的登陆凭证已超时,请重新登陆.";
    public static final String LEAVE_GAME_TITLE = "离开此页(&L)";
    @Resource
    private DmDdt defaultDm;

    @Resource
    private OfflineDetectionConfig offlineDetectionConfig;

    private final AbnormalDetectionDto abnormalDetectionDto = new AbnormalDetectionDto();

    public OfflineDetectionConfig get() {
        return this.offlineDetectionConfig;
    }

    public Boolean update(OfflineDetectionConfig offlineDetectionConfig) {
        if (offlineDetectionConfig == null) {
            throw new FrontException("参数为空");
        }

        offlineDetectionConfig.validMinDelay();
        this.offlineDetectionConfig.update(offlineDetectionConfig);
        this.offlineDetectionConfig.save();
        return true;
    }

    public AbnormalDetectionCountDto detect() {
        AbnormalDetectionCountDto abnormalDetectionCountDto = new AbnormalDetectionCountDto();
        if (Boolean.TRUE.equals(offlineDetectionConfig.getDisconnect())) {
            int[] hwnds = defaultDm.enumWindow(0, OFFLINE_DIALOG_MSG, "", DmConstants.EnumWindowFilter.TITLE);
            if (hwnds != null) {
                for (int hwnd : hwnds) {
                    if (abnormalDetectionDto.addDisconnect(hwnd)) {
                        abnormalDetectionCountDto.addDisconnect();
                    }
                }
            }
        }

        if (Boolean.TRUE.equals(offlineDetectionConfig.getOffsite())) {
            int[] hwnds = defaultDm.enumWindow(0, OFFSITE_LOGIN_MSG, "", DmConstants.EnumWindowFilter.TITLE);
            if (hwnds != null) {
                for (int hwnd : hwnds) {
                    if (abnormalDetectionDto.addOffsite(hwnd)) {
                        abnormalDetectionCountDto.addOffsite();
                    }
                }
            }
        }

        if (Boolean.TRUE.equals(offlineDetectionConfig.getTokenExpired())) {
            int[] hwnds = defaultDm.enumWindow(0, OFFSITE_LOGOUT_MSG, "", DmConstants.EnumWindowFilter.TITLE);
            if (hwnds != null) {
                for (int hwnd : hwnds) {
                    if (abnormalDetectionDto.addTokenExpired(hwnd)) {
                        abnormalDetectionCountDto.addTokenExpired();
                    }
                }
            }
        }

        if (Boolean.TRUE.equals(offlineDetectionConfig.getLeaveGame())) {
            int[] hwnds = defaultDm.enumWindow(0, LEAVE_GAME_TITLE, "", DmConstants.EnumWindowFilter.TITLE);
            if (hwnds != null) {
                for (int hwnd : hwnds) {
                    if (abnormalDetectionDto.addLeaveGame(hwnd)) {
                        abnormalDetectionCountDto.addLeaveGame();
                    }
                }
            }
        }

        if (Boolean.TRUE.equals(offlineDetectionConfig.getWhiteScreen())) {
            long now = System.currentTimeMillis();
            for (DmDdt dmDdt : GlobalVariable.DM_DDT_MAP.values()) {
                if (!dmDdt.isDdtWindow()) {
                    dmDdt.unbind();
                    continue;
                }
                String path = Constants.TEMP_GAME_SCREEN_SHOT_DIR + dmDdt.getHwnd() + "_" + now + ".png";
                dmDdt.captureFullGamePic(path);
                ImgUtil.MajorPixelInfo majorColor = ImgUtil.getMajorColor(path);
                Util.delayDeleteFile(path, null);
                if (majorColor == null || majorColor.getMajorPercent() < MAJOR_WHITE_SCREEN_THRESHOLD) {
                    continue;
                }

                if (ImgUtil.colorSimilar(majorColor.toColor(), WHITE_SCREEN_COLOR, WHITE_SCREEN_COLOR_DELTA)) {
                    if (abnormalDetectionDto.addWhiteScreen(dmDdt.getHwnd())) {
                        abnormalDetectionCountDto.addWhiteScreen();
                    }
                }
            }
        }

        return abnormalDetectionCountDto;
    }
}
