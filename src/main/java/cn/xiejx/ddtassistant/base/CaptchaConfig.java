package cn.xiejx.ddtassistant.base;

import cn.xiejx.ddtassistant.type.captcha.CaptchaConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/09/15 08:45
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class CaptchaConfig extends BaseConfig implements Serializable {
    private static final long serialVersionUID = 4417449558288421370L;

    private Tj tj;
    private Pc pc;

    /**
     * 是否开启低余额提醒
     */
    private Boolean lowBalanceRemind;
    /**
     * 低余额提醒余额值
     */
    private Double lowBalanceNum;

    private List<Integer> captchaWay;

    @Override
    public String getFileName() {
        return "验证码配置.json";
    }

    @Override
    public BaseConfig defaultConfig() {
        CaptchaConfig captchaConfig = new CaptchaConfig();

        List<Integer> way = new ArrayList<>();
        way.add(CaptchaChoiceEnum.PC.getChoice());
        way.add(CaptchaChoiceEnum.TJ.getChoice());
        way.add(CaptchaChoiceEnum.NONE.getChoice());
        way.add(CaptchaChoiceEnum.NONE.getChoice());
        way.add(CaptchaChoiceEnum.NONE.getChoice());
        way.add(CaptchaChoiceEnum.NONE.getChoice());
        way.add(CaptchaChoiceEnum.NONE.getChoice());
        captchaConfig.setCaptchaWay(way);

        Tj tj = new Tj();
        tj.setTypeId(Tj.DEFAULT_TYPE_ID);
        tj.setSoftId(Tj.DEFAULT_SOFT_ID);
        captchaConfig.setTj(tj);

        Pc pc = new Pc();
        captchaConfig.setPc(pc);

        return captchaConfig;
    }

    public enum CaptchaChoiceEnum {
        /**
         * 不打码
         */
        NONE(0, "无", 0),
        /**
         * 图鉴打码
         */
        TJ(1,"图鉴打码", CaptchaConstants.TJ_MIN_ANSWER_TIME),
        /**
         * 平川打码
         */
        PC(2, "服务器打码", CaptchaConstants.PC_MIN_ANSWER_TIME),
        ;
        private final Integer choice;
        private final String name;
        private final Integer minAnswerTime;

        CaptchaChoiceEnum(Integer choice,String name, Integer minAnswerTime) {
            this.choice = choice;
            this.name = name;
            this.minAnswerTime = minAnswerTime;
        }

        public Integer getChoice() {
            return choice;
        }

        public static CaptchaChoiceEnum getChoice(Integer choice) {
            if (choice == null) {
                return NONE;
            }
            for (CaptchaChoiceEnum captchaChoiceEnum : values()) {
                if (captchaChoiceEnum.getChoice().equals(choice)) {
                    return captchaChoiceEnum;
                }
            }
            return NONE;
        }

        public Integer getMinAnswerTime() {
            return minAnswerTime;
        }

        public String getName() {
            return name;
        }
    }

    @Data
    public static class Tj implements Serializable {
        private static final long serialVersionUID = 3701573224665113223L;

        public static final String DEFAULT_SOFT_ID = "3b995690b1794ff08bad1abb88a3e451";
        public static final String DEFAULT_TYPE_ID = "7";

        private String username;
        private String password;
        /**
         * 推荐码
         */
        private String softId;
        /**
         * 打码类型
         */
        private String typeId;

        public boolean validUserInfo() {
            return this.username != null && this.username.length() > 0 & this.password != null && this.password.length() > 0;
        }
    }

    @Data
    public static class Pc implements Serializable {
        private static final long serialVersionUID = 3701573224665113223L;

        private String username;
        private String password;
        private String code;
    }

    public void userConfigToCaptchaConfig(UserConfig userConfig) {
        if (new File(getFilePath()).exists()) {
            return;
        }
        log.info("迁移验证码配置...");

        setLowBalanceNum(userConfig.getLowBalanceNum());
        setLowBalanceRemind(userConfig.getLowBalanceRemind());
        this.tj.setSoftId(userConfig.getSoftId());
        this.tj.setPassword(userConfig.getPassword());
        this.tj.setUsername(userConfig.getUsername());
        this.tj.setTypeId(userConfig.getTypeId());
        save();
        log.info("验证码配置完成...");
    }
}
