package cn.xiejx.ddtassistant.test;

import cn.xiejx.ddtassistant.base.EmailConfig;
import cn.xiejx.ddtassistant.constant.Constants;
import cn.xiejx.ddtassistant.logic.EmailLogic;
import cn.xiejx.ddtassistant.type.captcha.Captcha;
import cn.xiejx.ddtassistant.type.captcha.LastCaptchaImg;
import cn.xiejx.ddtassistant.utils.ImageClean;
import cn.xiejx.ddtassistant.utils.OcrUtil;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import net.sourceforge.tess4j.TesseractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author sleepybear
 */
public class SimpleTest {
    private static final Logger log = LoggerFactory.getLogger(SimpleTest.class);

    public static void main(String[] args) throws Exception {
        testFile();
    }

    public static void testFile() {
        File file = new File("");
        System.out.println(file.getAbsoluteFile());
    }

    public static void sendEmail() {
        EmailConfig emailConfig = new EmailConfig();
        emailConfig.setRemoteSenderAddr("http://lh:17171/email/sendEmailByRemote");
        EmailLogic.sendRemoteEmail(emailConfig, "test", "body", 1L);
    }

    public static void testSamePic() throws IOException {
        BufferedImage image1 = ImageIO.read(new File("pictures/396134-16_54_02.png"));
        BufferedImage image2 = ImageIO.read(new File("pictures/396134-17_13_59.png"));
        BufferedImage image3 = ImageIO.read(new File("pictures/396134-17_14_00.png"));
        BufferedImage image4 = ImageIO.read(new File("pictures/396134-17_14_09.png"));
        BufferedImage[] bufferedImages = {image1, image2, image3, image4};
        for (BufferedImage bufferedImage : bufferedImages) {
            for (BufferedImage image : bufferedImages) {
                System.out.println(Captcha.isSameCaptcha(new LastCaptchaImg(bufferedImage, null), image));
            }
            System.out.println();
        }
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
