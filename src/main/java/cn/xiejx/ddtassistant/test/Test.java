package cn.xiejx.ddtassistant.test;

import cn.xiejx.ddtassistant.utils.Util;
import cn.xiejx.ddtassistant.utils.tj.TjHttpUtil;
import cn.xiejx.ddtassistant.utils.tj.TjPredictDto;
import cn.xiejx.ddtassistant.utils.tj.TjResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * @author sleepybear
 */
@Slf4j
public class Test {
    public static void main(String[] args) {

    }

    public static void r() {
        String captchaDir = "captcha/" + Util.getTimeString(Util.TIME_YMD_FORMAT);
        File file = new File(captchaDir);
        if (!file.isDirectory()) {
            System.out.println(file.mkdirs());
        }

    }

    public static void testYzm() {
        TjPredictDto tjPredictDto = new TjPredictDto("sleepybear", "tj123456", "7", null, null, "captcha-sample.jpg");
        TjResponse tjResponse = TjHttpUtil.getTjResponse(tjPredictDto);

        System.out.println(tjResponse);
    }

    public static void sleep(long t) {
        try {
            TimeUnit.MILLISECONDS.sleep(t);
        } catch (InterruptedException ignored) {

        }
    }
}
