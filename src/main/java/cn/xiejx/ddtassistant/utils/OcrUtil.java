package cn.xiejx.ddtassistant.utils;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
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

    public static Integer ocrCountDownPic(String path) {
        try {
            ITesseract instance = new Tesseract();
            BufferedImage bufferedImage = changeImgBgColor(path);
            instance.setDatapath("tessdata");
            instance.setLanguage("eng");
            String ocr = instance.doOCR(bufferedImage);
            if (StringUtils.isBlank(ocr)) {
                return null;
            }
            String res = ocr.trim().replace("b", "6").replace("T", "7").replace("s", "");
            if (StringUtils.isNumeric(res)) {
                return Integer.valueOf(res);
            }
        } catch (Exception e) {
            log.warn("OCR 图像识别失败：{}", e.getMessage());
            return null;
        }
        return null;
    }

    public static String ocrAuctionItemName(String path) {
        try {
            ITesseract instance = new Tesseract();
            BufferedImage bufferedImage = changeAuctionItemImg(path);
            instance.setDatapath("tessdata");
            instance.setLanguage("zh_ddt");
            String ocr = instance.doOCR(bufferedImage);
            ImageIO.write(bufferedImage, "png", new File("tmp/auction/10.png"));
            if (StringUtils.isBlank(ocr)) {
                return null;
            }
            return ocr.trim();
        } catch (Exception e) {
            log.warn("OCR 图像识别失败：{}", e.getMessage());
            return null;
        }
    }

    public static Integer ocrAuctionItemNum(String path) {
        try {
            ITesseract instance = new Tesseract();
            instance.setDatapath("tessdata");
            instance.setLanguage("eng");
            String ocr = instance.doOCR(new File(path));
            if (StringUtils.isBlank(ocr)) {
                return null;
            }
            String trim = ocr.trim();
            if (StringUtils.isNumeric(trim)) {
                return Integer.valueOf(trim);
            }
            return null;
        } catch (Exception e) {
            log.warn("OCR 图像识别失败：{}", e.getMessage());
            return null;
        }
    }

    public static BufferedImage changeAuctionItemImg(String path) throws IOException {
        BufferedImage img = ImageIO.read(new File(path));
        WritableRaster raster = img.getRaster();
        int width = img.getWidth();
        int height = img.getHeight();

        int[] delta = {40, 40, 40};
        int[] black = {30, 30, 30};
        int[] white1 = {150, 150, 150};
        int[] white2 = {240, 240, 240};
        int[] t = {150, 150, 150};

        for (int xx = 0; xx < width; xx++) {
            for (int yy = 0; yy < height; yy++) {
                int[] pixels = raster.getPixel(xx, yy, (int[]) null);
                changePixels(pixels, black, white2, delta, false);
                changePixels(pixels, white2, t, delta, false);
                changePixels(pixels, t, black, delta, false);
                changePixels(pixels, black, white2, delta, false);
                raster.setPixel(xx, yy, pixels);
            }
        }
        ImageIO.write(img, "png", new File("tmp/auction/5.png"));
        return img;
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
