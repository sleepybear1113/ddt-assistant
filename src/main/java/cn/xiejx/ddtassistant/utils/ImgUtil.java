package cn.xiejx.ddtassistant.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
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

    public static boolean colorInDelta(int[] color, int[] standard,int[] delta) {
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

    public static int[] getMajorColor(String path) throws IOException {
        BufferedImage img = ImageIO.read(new File(path));
        return getMajorColor(img);
    }

    public static int[] getMajorColor(BufferedImage img) {
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
        return new int[]{Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2])};
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
        String base = "D:\\XJXCode\\Java\\Spring\\ddt-assistant\\tmp\\ocr\\";
        mergeImgInDir(base + "b", base + "res/3.png");
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

    public enum DeltaInOut {
        /**
         * 1
         */
        DELTA_IN,
        DELTA_OUT,
    }
}
