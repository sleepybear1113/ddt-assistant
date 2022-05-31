package cn.xiejx.ddtassistant.utils;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
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
    public static ITesseract instance = new Tesseract();

    public static Integer ocrCountDownPic(String path) {
        try {
            BufferedImage bufferedImage = changeImgBgColor(path);
            instance.setDatapath("tessdata");
            instance.setLanguage("eng");
            String ocr = instance.doOCR(bufferedImage);
            if (StringUtils.isBlank(ocr)) {
                return null;
            }
            String res = ocr.replace("b", "6").replace("T", "7").replace("s", "");
            if (StringUtils.isNumeric(res)) {
                return Integer.valueOf(res);
            }
        } catch (Exception e) {
            log.warn("OCR 图像识别失败：{}", e.getMessage());
            return null;
        }
        return null;
    }

    public static BufferedImage changeImgBgColor(String path) throws IOException {
        int[] sampleColor = {255, 255, 255};
        int[] blue = {70, 180, 230};
        int[] delta = {40, 40, 40};
        int[] black = {0, 0, 0};
        BufferedImage img = ImageIO.read(new File(path));

        WritableRaster raster = img.getRaster();
        int width = img.getWidth();
        int height = img.getHeight();

        for (int xx = 0; xx < width; xx++) {
            for (int yy = 0; yy < height; yy++) {
                int[] pixels = raster.getPixel(xx, yy, (int[]) null);
                changePixels(pixels, blue, sampleColor, delta, false);
                changePixels(pixels, sampleColor, black, delta, false);
                raster.setPixel(xx, yy, pixels);
            }
        }
        return img;
    }

    public static void changePixels(int[] input, int[] sample, int[] target, int[] delta, boolean forceChange) {
        boolean flag = false;
        for (int i = 0; i < input.length; i++) {
            if (forceChange) {
                flag = true;
                break;
            }
            if (Math.abs(input[i] - sample[i]) > delta[i]) {
                flag = true;
                break;
            }
        }
        if (!flag) {
            return;
        }
        System.arraycopy(target, 0, input, 0, input.length);
    }
}