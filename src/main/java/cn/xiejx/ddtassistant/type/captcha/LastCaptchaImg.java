package cn.xiejx.ddtassistant.type.captcha;

import cn.xiejx.ddtassistant.utils.captcha.ChoiceEnum;
import lombok.Data;

import java.awt.image.BufferedImage;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/12/17 21:34
 */
@Data
public class LastCaptchaImg {
    private BufferedImage img;
    private ChoiceEnum choiceEnum;
    private Integer sameTimes = 0;

    public LastCaptchaImg() {
    }

    public LastCaptchaImg(BufferedImage img, ChoiceEnum choiceEnum) {
        this.img = img;
        this.choiceEnum = choiceEnum;
    }

    public void addSameTimes() {
        this.sameTimes++;
    }
}
