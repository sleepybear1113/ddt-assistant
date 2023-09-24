package cn.sleepybear.ddtassistant.utils;

import cn.sleepybear.ddtassistant.constant.Constants;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/06/01 00:30
 */
@Slf4j
public class OcrUtil {
    public static final String TESS_DATA_PATH = Constants.TESS_DATA_DIR;
    public static final String END_LANGUAGE = "eng";
    public static final String ZH_DDT_LANGUAGE = "zh_ddt";

    public static Integer ocrCountDownPic(String path) {
        try {
            int[] blue = {70, 180, 230};
            int[][][] colors = new int[][][]{
                    {blue, ImgUtil.WHITE, ImgUtil.COLOR_40},
                    {ImgUtil.WHITE, ImgUtil.BLACK, ImgUtil.COLOR_40},
            };
            BufferedImage bufferedImage = ImgUtil.changeImgColor(path, colors, ImgUtil.DeltaInOut.DELTA_OUT);

            String ocr = ocr(bufferedImage, END_LANGUAGE);
            if (StringUtils.isBlank(ocr)) {
                return null;
            }
            String res = ocr.trim().replace("b", "6").replace("T", "7").replace("s", "");
            if (StringUtils.isNumeric(res)) {
                return Integer.valueOf(res);
            }
        } catch (Exception e) {
            log.warn("OCR 图像识别失败：{}", e.getMessage(), e);
            return null;
        }
        return null;
    }

    public static String ocrAuctionItemName(String path) {
        return ocrAuctionItemName(path, null);
    }

    public static String ocrAuctionItemName(String path, String cleanImgPath) {
        try {
            int[] black = {30, 30, 30};
            int[] white = {240, 240, 240};
            int[] t = {150, 150, 150};
            int[][][] colors = new int[][][]{
                    {black, white, ImgUtil.COLOR_40},
                    {white, t, ImgUtil.COLOR_40},
                    {t, black, ImgUtil.COLOR_40},
                    {black, white, ImgUtil.COLOR_40},
            };
            BufferedImage bufferedImage = ImgUtil.changeImgColor(path, colors, ImgUtil.DeltaInOut.DELTA_OUT);

            String ocr = ocr(bufferedImage, ZH_DDT_LANGUAGE);

            if (StringUtils.isNotBlank(cleanImgPath)) {
                writeImg(bufferedImage, cleanImgPath);
            }

            if (StringUtils.isBlank(ocr)) {
                return null;
            }
            return ocr.trim();
        } catch (Exception e) {
            log.warn("OCR 图像识别失败：{}", e.getMessage(), e);
            return null;
        }
    }

    public static Integer ocrAuctionItemArguePrice(String path) {
        try {
            int[][][] colors = new int[][][]{
                    {{80, 40, 10}, ImgUtil.WHITE, ImgUtil.COLOR_30},
            };
            BufferedImage bufferedImage = ImgUtil.changeImgColor(path, colors, ImgUtil.DeltaInOut.DELTA_OUT);

            String ocr = ocr(bufferedImage, END_LANGUAGE);
            if (StringUtils.isBlank(ocr)) {
                return null;
            }
            String res = ocr.trim().replace("b", "6").replace("T", "7").replace("s", "");
            if (StringUtils.isNumeric(res)) {
                return Integer.valueOf(res);
            }
        } catch (Exception e) {
            log.warn("OCR 图像识别失败：{}", e.getMessage(), e);
            return null;
        }
        return null;
    }

    public static Integer ocrAuctionItemNum(String path) {
        try {
            BufferedImage bufferedImage = ImgUtil.changeImgColor(path, null, ImgUtil.DeltaInOut.DELTA_OUT);

            String ocr = ocr(bufferedImage, END_LANGUAGE);
            if (StringUtils.isBlank(ocr)) {
                return null;
            }
            String trim = ocr.trim();
            if (StringUtils.isNumeric(trim)) {
                return Integer.valueOf(trim);
            }
            return null;
        } catch (Exception e) {
            log.warn("OCR 图像识别失败：{}", e.getMessage(), e);
            return null;
        }
    }

    public static String ocr(BufferedImage bufferedImage) throws TesseractException {
        return ocr(bufferedImage, END_LANGUAGE);
    }

    public static String ocr(BufferedImage bufferedImage, String language) throws TesseractException {
        ITesseract instance = new Tesseract();
        instance.setDatapath(TESS_DATA_PATH);
        instance.setLanguage(language);
        return instance.doOCR(bufferedImage);
    }

    public static String ocr(String path, String language) throws TesseractException {
        ITesseract instance = new Tesseract();
        instance.setDatapath(TESS_DATA_PATH);
        instance.setLanguage(language);
        return instance.doOCR(new File(path));
    }

    public static String ocr(String path) throws TesseractException {
        return ocr(path, END_LANGUAGE);
    }

    public static boolean testOcr() {
        String location = Constants.CAPTCHA_TEST_DIR + "test-1.png";
        try {
            BufferedImage image = ImageIO.read(new File(location));
            // 在这里可以对图像进行处理或使用
            String ocr = ocr(image, END_LANGUAGE);
            return true;
        } catch (Error | Exception e) {
            log.info(e.getMessage(), e);
            return false;
        }
    }

    public static void writeImg(BufferedImage bufferedImage, String path) throws IOException {
        String[] split = path.split("\\.");
        ImageIO.write(bufferedImage, split[split.length - 1], new File(path));
    }

    public static void main(String[] args) throws TesseractException {
        testOcr();
    }
}
