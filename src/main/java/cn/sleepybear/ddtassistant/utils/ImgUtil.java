package cn.sleepybear.ddtassistant.utils;

import cn.sleepybear.ddtassistant.dm.DmDdt;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.*;

/**
 * @author sleepybear
 */
@Slf4j
public class ImgUtil {
    public static final int[] COLOR_0 = {0, 0, 0};
    public static final int[] COLOR_10 = {10, 10, 10};
    public static final int[] COLOR_20 = {20, 20, 20};
    public static final int[] COLOR_30 = {30, 30, 30};
    public static final int[] COLOR_40 = {40, 40, 40};
    public static final int[] COLOR_70 = {70, 70, 70};
    public static final int[] COLOR_140 = {140, 140, 140};
    public static final int[] BLACK = {0, 0, 0};
    public static final int[] WHITE = {255, 255, 255};

    public static BufferedImage read(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            return null;
        }
    }

    public static boolean colorSimilar(int[] target, int[] test, int[] delta) {
        return Math.abs(target[0] - test[0]) < delta[0] && Math.abs(target[1] - test[1]) < delta[1] && Math.abs(target[2] - test[2]) < delta[2];
    }

    public static void cleanImg(String from, String to) {
        cleanImg(from, to, ImageClean.Type.CAPTCHA_NORMAL);
    }

    public static void cleanImg(String from, String to, ImageClean.Type type) {
        ImageClean imageClean = new ImageClean(type, 1.0, 1.0, 0);
        imageClean.cleanImage(from, to);
    }

    public static BufferedImage changeImgColor(String path, int[][][] colors, DeltaInOut deltaInOut) throws IOException {
        BufferedImage img = ImageIO.read(new File(path));
        return changeImgColor(img, colors, deltaInOut);
    }

    public static BufferedImage changeImgColor(BufferedImage img, int[][][] colors, DeltaInOut deltaInOut) {
        if (colors == null || colors.length == 0) {
            return img;
        }

        List<int[][]> colorList = new ArrayList<>();
        for (int[][] color : colors) {
            if (color == null || color.length <= 1) {
                continue;
            }
            int[][] m = new int[3][3];
            m[0] = color[0];
            m[1] = color[1];
            if (color.length >= 3) {
                m[2] = color[2];
            } else {
                m[2] = COLOR_0;
            }
            colorList.add(m);
        }
        if (CollectionUtils.isEmpty(colorList)) {
            return img;
        }

        WritableRaster raster = img.getRaster();
        int width = img.getWidth();
        int height = img.getHeight();

        for (int xx = 0; xx < width; xx++) {
            for (int yy = 0; yy < height; yy++) {
                int[] pixels = raster.getPixel(xx, yy, (int[]) null);

                for (int[][] color : colorList) {
                    changePixels(pixels, color[0], color[1], color[2], deltaInOut, false);
                }
                raster.setPixel(xx, yy, pixels);
            }
        }
        return img;
    }

    public static void changePixels(int[] input, int[] sample, int[] target, int[] delta, DeltaInOut deltaInOut, boolean forceChange) {
        boolean flag = false;
        for (int i = 0; i < input.length; i++) {
            if (forceChange) {
                flag = true;
                break;
            }
            int d = deltaInOut == DeltaInOut.DELTA_OUT ? 1 : -1;
            if (d * (Math.abs(input[i] - sample[i]) - delta[i]) >= 0) {
                flag = true;
                break;
            }
        }
        if (!flag) {
            return;
        }
        System.arraycopy(target, 0, input, 0, input.length);
    }

    public static boolean colorInDelta(int[] color, int[] standard, int[] delta) {
        if (color == null) {
            return false;
        }
        if (standard == null) {
            return true;
        }
        if (delta == null) {
            delta = new int[]{0, 0, 0};
        }
        return Math.abs(color[0] - standard[0]) < delta[0] && Math.abs(color[1] - standard[1]) < delta[1] && Math.abs(color[2] - standard[2]) < delta[2];
    }

    public static int[] getAvgColor(String path) {
        try {
            BufferedImage img = ImageIO.read(new File(path));
            return getAvgColor(img);
        } catch (IOException e) {
            return null;
        }
    }

    public static int[] getAvgColor(BufferedImage img) {
        if (img == null) {
            return null;
        }

        WritableRaster raster = img.getRaster();
        int width = img.getWidth();
        int height = img.getHeight();
        List<int[]> list = new ArrayList<>();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int[] pixels = raster.getPixel(x, y, (int[]) null);
                list.add(pixels);
            }
        }

        if (CollectionUtils.isEmpty(list)) {
            return new int[]{0, 0, 0};
        }

        int size = list.size();
        long[] totalColor = {0, 0, 0};
        for (int[] color : list) {
            int i = -1;
            totalColor[++i] += color[i];
            totalColor[++i] += color[i];
            totalColor[++i] += color[i];
        }
        return new int[]{(int) (totalColor[0] / size), (int) (totalColor[1] / size), (int) (totalColor[2] / size)};
    }

    public static MajorPixelInfo getMajorColor(String path) {
        try {
            BufferedImage img = ImageIO.read(new File(path));
            return getMajorColor(img);
        } catch (IOException e) {
            return null;
        }
    }

    public static MajorPixelInfo getMajorColor(BufferedImage img) {
        if (img == null) {
            return null;
        }

        WritableRaster raster = img.getRaster();
        int width = img.getWidth();
        int height = img.getHeight();
        Map<String, Integer> colorCountMap = new HashMap<>();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int[] pixels = raster.getPixel(x, y, (int[]) null);
                String s = Arrays.toString(pixels);
                Integer count = colorCountMap.get(s);
                if (count == null) {
                    colorCountMap.put(s, 1);
                } else {
                    colorCountMap.put(s, ++count);
                }
            }
        }

        if (MapUtils.isEmpty(colorCountMap)) {
            return null;
        }

        String majorColor = null;
        int majorCount = 0;
        for (String key : colorCountMap.keySet()) {
            Integer count = colorCountMap.get(key);
            if (majorCount < count) {
                majorColor = key;
                majorCount = count;
            }
        }

        if (StringUtils.isBlank(majorColor)) {
            return null;
        }

        String[] split = majorColor.replace("[", "").replace("]", "").replace(" ", "").split(",");
        MajorPixelInfo majorPixelInfo = new MajorPixelInfo(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
        majorPixelInfo.setWidth(img.getWidth());
        majorPixelInfo.setHeight(img.getHeight());
        majorPixelInfo.setMajorCount(majorCount);
        return majorPixelInfo;
    }

    public static boolean imgConvert(String from, String to) {
        try {
            BufferedImage img = ImageIO.read(new File(from));
            String[] split = to.split("\\.");
            String end = split[split.length - 1];
            ImageIO.write(img, end, new File(to));
            return true;
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
            return false;
        }
    }

    public static void main(String[] args) {
        mosaicPevIncomeName("D:\\XJXCode\\Java\\Spring\\ddt-assistant\\test\\124045.jpg", "test/mosaic-1.jpg");
    }

    public static void mergeImgInDir(String dir, String outputPath) {
        File imgDir = new File(dir);
        if (!imgDir.exists()) {
            return;
        }
        File[] files = imgDir.listFiles();
        if (files == null || files.length == 0) {
            return;
        }

        List<String> fileNameList = new ArrayList<>();
        for (File file : files) {
            if (!file.isFile()) {
                continue;
            }
            fileNameList.add(file.getPath());
        }
        try {
            int len = fileNameList.size();
            if (len < 1) {
                System.out.println("pics len < 1");
                return;
            }
            File[] src = new File[len];
            BufferedImage[] images = new BufferedImage[len];
            int[][] imageArrays = new int[len][];
            for (int i = 0; i < len; i++) {
                src[i] = new File(fileNameList.get(i));
                images[i] = ImageIO.read(src[i]);
                int width = images[i].getWidth();
                int height = images[i].getHeight();
                // 从图片中读取RGB
                imageArrays[i] = new int[width * height];
                imageArrays[i] = images[i].getRGB(0, 0, width, height, imageArrays[i], 0, width);
            }

            int dstHeight = 0;
            int dstWidth = images[0].getWidth();
            for (BufferedImage image : images) {
                dstWidth = Math.max(dstWidth, image.getWidth());
                dstHeight += image.getHeight();
            }
            if (dstHeight < 1) {
                System.out.println("dst_height < 1");
                return;
            }
            /*
             * 生成新图片
             */
            BufferedImage imageNew = new BufferedImage(dstWidth, dstHeight, BufferedImage.TYPE_INT_RGB);
            int height = 0;
            for (int i = 0; i < images.length; i++) {
                imageNew.setRGB(0, height, dstWidth, images[i].getHeight(), imageArrays[i], 0, dstWidth);
                height += images[i].getHeight();
            }
            File outFile = new File(outputPath);
            String[] split = outputPath.split("\\.");
            ImageIO.write(imageNew, split[split.length - 1], outFile);
            System.out.println(1111);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void compress(String from, String dest, float quality) {
        try {
            compress(ImageIO.read(new File(from)), dest, quality);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void compress(BufferedImage image, String dest, float quality) {
        try {
            File compressedImageFile = new File(dest);
            OutputStream os = Files.newOutputStream(compressedImageFile.toPath());
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");

            ImageWriter writer = writers.next();
            ImageOutputStream ios = ImageIO.createImageOutputStream(os);
            writer.setOutput(ios);
            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quality);
            writer.write(null, new IIOImage(image, null, null), param);

            os.close();
            ios.close();
            writer.dispose();
        } catch (IOException e) {
            log.info("压缩图片失败，{}，{}", dest, e.getMessage());
        }
    }

    public static void mosaicPevIncomeName(String path, String to) {
        if (StringUtils.isBlank(path)) {
            return;
        }

        try {
            BufferedImage image = ImageIO.read(new File(path));
            int height = image.getHeight();
            int width = image.getWidth();
            if (height < DmDdt.GAME_FULL_REACT[3] || width < DmDdt.GAME_FULL_REACT[2]) {
                return;
            }

            int mosaicHeight = 22;
            int mosaicWidth = 96;
            int rgb = new Color(180, 60, 16).getRGB();
            int[] rgbList = new int[mosaicHeight * mosaicWidth];
            Arrays.fill(rgbList, rgb);

            int xGap = 132;
            int yGap = 179;
            int startX = 55;
            int startY = 200;

            for (int i = 0; i < 7; i++) {
                for (int j = 0; j < 3; j++) {
                    image.setRGB(startX + i * xGap, startY + j * yGap, mosaicWidth, mosaicHeight, rgbList, 0, 0);
                }
            }
            String[] split = path.split("\\.");
            ImageIO.write(image, split[split.length - 1], new File(to));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public enum DeltaInOut {
        /**
         * 1
         */
        DELTA_IN,
        DELTA_OUT,
    }

    @Getter
    public static class MajorPixelInfo {
        private int r, g, b;
        private int width, height;
        private int majorCount;

        public MajorPixelInfo() {
        }

        public MajorPixelInfo(int r, int g, int b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }

        public int[] toColor() {
            return new int[]{r, g, b};
        }

        public void setR(int r) {
            this.r = r;
        }

        public void setG(int g) {
            this.g = g;
        }

        public void setB(int b) {
            this.b = b;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public void setMajorCount(int majorCount) {
            this.majorCount = majorCount;
        }

        public double getMajorPercent() {
            return majorCount * 1.0 / width / height;
        }

        @Override
        public String toString() {
            return "MajorPixelInfo{" +
                    "r=" + r +
                    ", g=" + g +
                    ", b=" + b +
                    ", " + width +
                    "*" + height +
                    "=" + width * height +
                    ", majorCount=" + majorCount +
                    ", percent=" + Double.valueOf(String.format("%.2f", getMajorPercent() * 100)) + "%" +
                    '}';
        }
    }

}
