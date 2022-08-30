package cn.xiejx.ddtassistant.test;

import cn.xiejx.ddtassistant.constant.Constants;
import cn.xiejx.ddtassistant.type.captcha.Captcha;
import cn.xiejx.ddtassistant.utils.ImageClean;
import cn.xiejx.ddtassistant.utils.OcrUtil;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author sleepybear
 */
public class SimpleTest {
    public static void main(String[] args) throws Exception {
        captchaValid();
    }

    public static void captchaValid() {
        System.out.println(Captcha.captchaValid("图片/A@67200-12_59_39.png"));
        System.out.println(Captcha.captchaValid("图片/2.bmp"));
    }

    public static void clean1() {
        ImageClean imageClean = new ImageClean(ImageClean.Type.CAPTCHA_WHITE_CHAR, 1.0, 1.0, 0);
        String s = "461402.png";
        String src = "test/src/" + s;
        String dst = "test/dst/" + s;
        imageClean.cleanImage(Constants.TEMP_DIR + "ocr/raw/1657723842466.png", Constants.TEMP_DIR + "ocr/raw/1657723842466-1.png");
    }

    public static void ocr() throws TesseractException {
        System.out.println(OcrUtil.ocr(Constants.TEMP_DIR + "ocr/raw/1657723842466-1.png", "chi_sim"));
    }

    public static void testJson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);

        System.out.println(mapper.readValue("{\"a\":\"\"}}", Aq.class));
    }

    public static boolean testAdmin() {
        File file = new File("C:\\测试管理员空文件-1113.txt");
        if (file.exists()) {
            return file.delete();
        } else {
            try {
                return file.createNewFile();
            } catch (IOException e) {
                return false;
            }
        }
    }

    @Data
    static class Aq {
        @JsonAnySetter
        public Map<String, String> a;
    }
}
