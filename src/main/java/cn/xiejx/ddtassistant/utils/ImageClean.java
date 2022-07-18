package cn.xiejx.ddtassistant.utils;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

/**
 * 验证码图片过滤清理
 *
 * @author Ray
 * @author inthinkcolor@gmail.com
 * @author easyproject.cn
 * @version 3.0.3
 */
public class ImageClean {
    /**
     * 图片清理方法反射缓存
     */
    private final Map<String, Method> cleanMethodsCache = new HashMap<>();
    /**
     * 执行图片清理方法的实例缓存
     */
    private final Map<String, Object> cleanMethodsInstanceCache = new HashMap<>();
    /**
     * 反射出的类实例缓存
     */
    private final Map<String, Object> instanceCache = new HashMap<>();
    /**
     * 被处理的图片对象
     */
    protected BufferedImage image;
    /**
     * 图片宽度
     */
    private int iw;

    /**
     * 图片高度
     */
    private int ih;
    /**
     * 图片像素数组
     */
    private int[] pixels;

    /**
     * 图片类型，默认为：IMAGE_TYPE_CAPTCHA_NORMAL
     */
    private Type imageType = Type.CAPTCHA_NORMAL;

    /**
     * 图片宽度形变比例，某些图片形变后识别率可以提高，1为原始比例，不形变
     */
    private double imageWidthRatio = 1;

    /**
     * 图片高度形变比例，某些图片形变后识别率可以提高，1为原始比例，不形变
     */
    private double imageHeightRatio = 1;

    /**
     * 图片顺时针旋转角度，默认为0，不旋转
     */
    private int degrees = 0;

    private static int i = 0; // 递归次数

    private static final int recursionMax = 100; // 每次递归最大次数

    /**
     * 检测坐标是否在有效范围
     *
     * @param r 行
     * @param c 列
     * @param w 宽
     * @param h 高
     * @return 是否是有效坐标
     */
    private static boolean checkPixelRange(int r, int c, int w, int h) {
        return r >= 0 && r <= h - 1 && c >= 0 && c <= w - 1;
    }

    /**
     * 空心文字边框递归清除
     *
     * @param pixelsArray 空心文字图片像素数组
     * @param r           行
     * @param c           列
     * @param w           宽
     * @param h           高
     */
    private static void searchAdjacentRecursion(int[][] pixelsArray, int r,
                                                int c, int w, int h) {
        i++;
        if (i > recursionMax) {
            i = 0;
            return;
        }
        // 是否需要递归检测标识
        boolean recursionUp = false;
        boolean recursionDown = false;
        boolean recursionLeft = false;
        boolean recursionRight = false;
        boolean recursionRightUp = false;
        boolean recursionRightDown = false;
        boolean recursionLeftUp = false;
        boolean recursionLeftDown = false;

        // 上下左右行列坐标
        int rUp = r - 1;

        int rDown = r + 1;

        int cLeft = c - 1;

        int cRight = c + 1;

        int rRightUp = r - 1;
        int cRightUp = c + 1;
        int rRightDown = r + 1;
        int cRightDown = c + 1;

        int rLeftUp = r - 1;
        int cLeftUp = c - 1;
        int rLeftDown = r + 1;
        int cLeftDown = c - 1;

        if (checkPixelRange(rUp, c, w, h)) {
            if (pixelsArray[rUp][c] != -1) {
                pixelsArray[rUp][c] = -1;
                recursionUp = true;
            }
        }
        if (checkPixelRange(rDown, c, w, h)) {
            if (pixelsArray[rDown][c] != -1) {
                pixelsArray[rDown][c] = -1;
                recursionDown = true;
            }
        }
        if (checkPixelRange(r, cLeft, w, h)) {
            if (pixelsArray[r][cLeft] != -1) {
                pixelsArray[r][cLeft] = -1;
                recursionLeft = true;
            }
        }
        if (checkPixelRange(r, cRight, w, h)) {
            if (pixelsArray[r][cRight] != -1) {
                pixelsArray[r][cRight] = -1;
                recursionRight = true;
            }
        }
        if (checkPixelRange(rRightUp, cRightUp, w, h)) {
            if (pixelsArray[rRightUp][cRightUp] != -1) {
                pixelsArray[rRightUp][cRightUp] = -1;
                recursionRightUp = true;
            }
        }
        if (checkPixelRange(rRightDown, cRightDown, w, h)) {
            if (pixelsArray[rRightDown][cRightDown] != -1) {
                pixelsArray[rRightDown][cRightDown] = -1;
                recursionRightDown = true;
            }
        }
        if (checkPixelRange(rLeftUp, cLeftUp, w, h)) {
            if (pixelsArray[rLeftUp][cLeftUp] != -1) {
                pixelsArray[rLeftUp][cLeftUp] = -1;
                recursionLeftUp = true;
            }
        }
        if (checkPixelRange(rLeftDown, cLeftDown, w, h)) {
            if (pixelsArray[rLeftDown][cLeftDown] != -1) {
                pixelsArray[rLeftDown][cLeftDown] = -1;
                recursionLeftDown = true;
            }
        }

        if (recursionUp) {
            searchAdjacentRecursion(pixelsArray, rUp, c, w, h);
        }
        if (recursionDown) {
            searchAdjacentRecursion(pixelsArray, rDown, c, w, h);
        }
        if (recursionLeft) {
            searchAdjacentRecursion(pixelsArray, r, cLeft, w, h);
        }
        if (recursionRight) {
            searchAdjacentRecursion(pixelsArray, r, cRight, w, h);
        }
        if (recursionRightUp) {
            searchAdjacentRecursion(pixelsArray, rRightUp, cRightUp, w, h);
        }
        if (recursionRightDown) {
            searchAdjacentRecursion(pixelsArray, rRightDown, cRightDown, w, h);
        }
        if (recursionLeftUp) {
            searchAdjacentRecursion(pixelsArray, rLeftUp, cLeftUp, w, h);
        }
        if (recursionLeftDown) {
            searchAdjacentRecursion(pixelsArray, rLeftDown, cLeftDown, w, h);
        }
    }

    /**
     * 无参构造方法 图 片类型默认为 ImageClean.IMAGE_TYPE_CAPTCHA_NORMAL : int 图片宽高比例默认为1
     */
    public ImageClean() {
    }

    /**
     * 有参构造方法
     * <p>
     * 图片类型默认为 ImageClean.IMAGE_TYPE_CAPTCHA_NORMAL : int
     *
     * @param imageWidthRatio  图片宽度形变比例
     * @param imageHeightRatio 图片高度形变比例
     */
    public ImageClean(double imageWidthRatio, double imageHeightRatio) {
        super();
        this.imageWidthRatio = imageWidthRatio;
        this.imageHeightRatio = imageHeightRatio;
    }

    /**
     * 有参构造方法
     * <p>
     * 图片类型默认为 ImageClean.IMAGE_TYPE_CAPTCHA_NORMAL : int
     *
     * @param imageWidthRatio  图片宽度形变比例
     * @param imageHeightRatio 图片高度形变比例
     * @param degrees          图片顺时针旋转角度
     */
    public ImageClean(double imageWidthRatio, double imageHeightRatio,
                      int degrees) {
        super();
        this.imageWidthRatio = imageWidthRatio;
        this.imageHeightRatio = imageHeightRatio;
        this.degrees = degrees;
    }

    /**
     * 有参构造方法 图片宽高比例默认为1
     *
     * @param imageType 指定图片默认类型
     */
    public ImageClean(Type imageType) {
        this.imageType = imageType;
    }

    /**
     * 有参构造方法
     *
     * @param imageType        指定图片默认类型
     * @param imageWidthRatio  图片宽度形变比例
     * @param imageHeightRatio 图片高度形变比例
     */
    public ImageClean(Type imageType, double imageWidthRatio,
                      double imageHeightRatio) {
        this.imageType = imageType;
        this.imageWidthRatio = imageWidthRatio;
        this.imageHeightRatio = imageHeightRatio;
    }

    /**
     * 有参构造方法
     *
     * @param imageType        指定图片默认类型
     * @param imageWidthRatio  图片宽度形变比例
     * @param imageHeightRatio 图片高度形变比例
     * @param degrees          图片顺时针旋转角度
     */
    public ImageClean(Type imageType, double imageWidthRatio,
                      double imageHeightRatio, int degrees) {
        super();
        this.imageType = imageType;
        this.imageWidthRatio = imageWidthRatio;
        this.imageHeightRatio = imageHeightRatio;
        this.degrees = degrees;
    }

    /**
     * 有参构造方法
     *
     * @param imageType 指定图片默认类型
     * @param degrees   图片顺时针旋转角度
     */
    public ImageClean(Type imageType, int degrees) {
        super();
        this.imageType = imageType;
        this.degrees = degrees;
    }

    /**
     * 有参构造方法
     *
     * @param degrees 顺时针旋转角度
     */
    public ImageClean(int degrees) {
        super();
        this.degrees = degrees;
    }

    /**
     * Brighten using a linear formula that increases all color values
     *
     * @return 图片对象
     */
    protected BufferedImage changeBrighten() {
        BufferedImage bufferedImage = new BufferedImage(iw, ih,
                BufferedImage.TYPE_INT_RGB);
        bufferedImage.getGraphics().drawImage(image, 0, 0, null);
        RescaleOp rop = new RescaleOp(1.0F, 0, null);
        return image = rop.filter(bufferedImage, null);
    }

    /**
     * Rotate the image 180 degrees about its center point
     *
     * @param degrees 角度
     * @return 图片对象
     */
    protected BufferedImage changeRotate(int degrees) {

        if (degrees % 360 == 0) {
            return image;
        }

        int w;
        int h;
        int x;
        int y;
        degrees = degrees % 360;
        if (degrees < 0) {
            degrees = 360 + degrees;// 将角度转换到0-360度之间
        }
        double ang = Math.toRadians(degrees);// 将角度转为弧度

        /*
         * 确定旋转后的图象的高度和宽度
         */
        if (degrees == 180 || degrees == 0 || degrees == 360) {
            w = iw;
            h = ih;
        } else if (degrees == 90 || degrees == 270) {
            w = ih;
            h = iw;
        } else {
            w = (int) (ih / 2 * Math.cos(ang)) + iw;
            h = (int) (iw / 2 * Math.sin(ang)) + ih;
        }

        x = (w / 2) - (iw / 2);// 确定原点坐标
        y = (h / 2) - (ih / 2);
        BufferedImage rotatedImage = new BufferedImage(w, h, image.getType());
        Graphics2D gs = rotatedImage.createGraphics();
        gs.setBackground(Color.WHITE);
        gs.fillRect(0, 0, w, h);
        AffineTransform at = new AffineTransform();
        at.rotate(ang, w * 1.0 / 2, h * 1.0 / 2);// 旋转图象
        at.translate(x, y);
        AffineTransformOp op = new AffineTransformOp(at,
                AffineTransformOp.TYPE_BICUBIC);
        op.filter(image, rotatedImage);
        iw = w;
        ih = h;
        return image = rotatedImage;
    }

    /**
     * 图片形变：某些图片形变后识别率可以提高
     *
     * @param imageWidthRatio  宽度形变比例
     * @param imageHeightRatio 高度形变比例
     * @return 图片对象
     */
    protected BufferedImage changeScaled(double imageWidthRatio,
                                         double imageHeightRatio) {
        // 形变：某些图片形变后识别率可以提高
        BufferedImage imageChangeSize = null;
        if (imageWidthRatio != 1 || imageHeightRatio != 1) {
            int w = iw;
            int h = ih;
            if (imageWidthRatio != 1) {
                w = (int) (iw * imageWidthRatio);
            }
            if (imageHeightRatio != 1) {
                h = (int) (ih * imageHeightRatio);
            }
            // image.getScaledInstance(w, h,Image.SCALE_SMOOTH);
            // BufferedImage imageChangeSize = new BufferedImage(w, h,
            // BufferedImage.TYPE_INT_RGB);
            // Graphics2D g = imageChangeSize.createGraphics();
            // AffineTransform at = AffineTransform.getScaleInstance(
            // imageWidthRatio, imageHeightRatio);
            // g.drawRenderedImage(image, at);

            // 新图片比例放大操作
            int type = (image.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB
                    : BufferedImage.TYPE_INT_ARGB;
            imageChangeSize = new BufferedImage(w, h, type);
            Graphics2D g = imageChangeSize.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.drawImage(image, 0, 0, w, h, null);
            g.dispose();
            image = imageChangeSize;
        }
        return imageChangeSize;
    }

    /**
     * 二值化，取图片的平均灰度作为阈值，二值化的域值，默认值为100，低于该值的全都为0，高于该值的全都为255。
     * 将整个图像呈现出明显的只有黑和白的视觉效果。
     *
     * @return 图片对象
     */
    protected BufferedImage changeToBinarization() {
        // 设定二值化的域值，默认值为100
        int grey = 100;
        return changeToBinarization(grey);
    }


    /**
     * 二值化，取图片的平均灰度作为阈值，低于该值的全都为0，高于该值的全都为255。 将整个图像呈现出明显的只有黑和白的视觉效果。
     *
     * @param grey 指定二值化的域值，低于该值的全都为0，高于该值的全都为255。
     * @return 图片对象
     */
    protected BufferedImage changeToBinarization(int grey) {
        PixelGrabber pg = new PixelGrabber(image.getSource(), 0, 0, iw, ih,
                pixels, 0, iw);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 对图像进行二值化处理，Alpha值保持不变
        ColorModel cm = ColorModel.getRGBdefault();
        for (int i = 0; i < iw * ih; i++) {
            int red, green, blue;
            int alpha = cm.getAlpha(pixels[i]);
            if (cm.getRed(pixels[i]) > grey) {
                red = 255;
            } else {
                red = 0;
            }
            if (cm.getGreen(pixels[i]) > grey) {
                green = 255;
            } else {
                green = 0;
            }
            if (cm.getBlue(pixels[i]) > grey) {
                blue = 255;
            } else {
                blue = 0;
            }
            pixels[i] = alpha << 24 | red << 16 | green << 8 | blue; // 通过移位重新构成某一点像素的RGB值
        }
        // 将数组中的象素产生一个图像
        Image tempImg = Toolkit.getDefaultToolkit().createImage(
                new MemoryImageSource(iw, ih, pixels, 0, iw));
        image = new BufferedImage(tempImg.getWidth(null),
                tempImg.getHeight(null), BufferedImage.TYPE_INT_BGR);
        image.createGraphics().drawImage(tempImg, 0, 0, null);
        return image;

    }

    /**
     * 变图像为黑白色 提示: 黑白化之前最好灰色化以便得到好的灰度平均值,利于获得好的黑白效果
     *
     * @return 图片对象
     */
    protected BufferedImage changeToBlackWhiteImage() {
        int avgGrayValue = getAvgValue(iw, ih);
        int whitePoint = getWhitePoint(), blackPoint = getBlackPoint();

        Color point;
        for (int i = 0; i < ih; i++) {
            for (int j = 0; j < iw; j++) {
                point = new Color(image.getRGB(j, i));
//				image.setRGB(j, i, (point.getRed() + point.getGreen() + point.getBlue()!=765 ? blackPoint
//						: whitePoint));
                // try {
                image.setRGB(j, i, (point.getRed() < avgGrayValue ? blackPoint : whitePoint));
                // } catch (Exception e) {
                // System.out.println("E2");
                // }
            }
        }
        return image;
    }


    /**
     * 使用RGB平均值将图像变为灰色，取像素点的rgb三色平均值作为灰度值
     *
     * @return 图片对象
     */
    protected BufferedImage changeToGrayByAvgColor() {
        int gray;
        Color point;
        for (int i = 0; i < ih; i++) {
            for (int j = 0; j < iw; j++) {
                point = new Color(image.getRGB(j, i));
                gray = (point.getRed() + point.getGreen() + point.getBlue()) / 3;
                try {
                    image.setRGB(j, i, new Color(gray, gray, gray).getRGB());
                } catch (Exception e) {

                    e.printStackTrace();
                }
            }
        }
        return image;
    }

    /**
     * 灰化，使用滤镜
     *
     * @return 图片对象
     */
    protected BufferedImage changeToGrey() {
        ColorConvertOp ccp = new ColorConvertOp(
                ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
        return image = ccp.filter(image, null);
    }

    /**
     * 中值滤波，消除图像椒盐噪音非常有效的非线性平滑技术。 将每一象素点的灰度值设置为该点某邻域窗口内的所有象素点灰度值的中值.
     * 中值滤波法对消除椒盐噪音非常有效，在光学测量条纹图象的相位分析处理方法中有特殊作用，但在条纹中心分析方法中作用不大.
     *
     * @return 图片对象
     */
    protected BufferedImage changeToMedian() {
        PixelGrabber pg = new PixelGrabber(image.getSource(), 0, 0, iw, ih,
                pixels, 0, iw);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 对图像进行中值滤波，Alpha值保持不变
        ColorModel cm = ColorModel.getRGBdefault();
        for (int i = 1; i < ih - 1; i++) {
            for (int j = 1; j < iw - 1; j++) {
                int red, green, blue;
                int alpha = cm.getAlpha(pixels[i * iw + j]);

                // int red2 = cm.getRed(pixels[(i - 1) * iw + j]);
                int red4 = cm.getRed(pixels[i * iw + j - 1]);
                int red5 = cm.getRed(pixels[i * iw + j]);
                int red6 = cm.getRed(pixels[i * iw + j + 1]);
                // int red8 = cm.getRed(pixels[(i + 1) * iw + j]);

                // 水平方向进行中值滤波
                if (red4 >= red5) {
                    if (red5 >= red6) {
                        red = red5;
                    } else {
                        red = Math.min(red4, red6);
                    }
                } else {
                    if (red4 > red6) {
                        red = red4;
                    } else {
                        red = Math.min(red5, red6);
                    }
                }

                int green4 = cm.getGreen(pixels[i * iw + j - 1]);
                int green5 = cm.getGreen(pixels[i * iw + j]);
                int green6 = cm.getGreen(pixels[i * iw + j + 1]);

                // 水平方向进行中值滤波
                if (green4 >= green5) {
                    if (green5 >= green6) {
                        green = green5;
                    } else {
                        green = Math.min(green4, green6);
                    }
                } else {
                    if (green4 > green6) {
                        green = green4;
                    } else {
                        green = Math.min(green5, green6);
                    }
                }

                // int blue2 = cm.getBlue(pixels[(i - 1) * iw + j]);
                int blue4 = cm.getBlue(pixels[i * iw + j - 1]);
                int blue5 = cm.getBlue(pixels[i * iw + j]);
                int blue6 = cm.getBlue(pixels[i * iw + j + 1]);
                // int blue8 = cm.getBlue(pixels[(i + 1) * iw + j]);

                // 水平方向进行中值滤波
                if (blue4 >= blue5) {
                    if (blue5 >= blue6) {
                        blue = blue5;
                    } else {
                        blue = Math.min(blue4, blue6);
                    }
                } else {
                    if (blue4 > blue6) {
                        blue = blue4;
                    } else {
                        blue = Math.min(blue5, blue6);
                    }
                }
                pixels[i * iw + j] = alpha << 24 | red << 16 | green << 8
                        | blue;
            }
        }

        // 将数组中的象素产生一个图像
        Image tempImg = Toolkit.getDefaultToolkit().createImage(
                new MemoryImageSource(iw, ih, pixels, 0, iw));
        image = new BufferedImage(tempImg.getWidth(null),
                tempImg.getHeight(null), BufferedImage.TYPE_INT_BGR);
        image.createGraphics().drawImage(tempImg, 0, 0, null);
        return image;

    }

    /**
     * 图片过滤处理
     *
     * @param from 要处理的图片文件
     * @param to   图片输出文件
     * @return 处理结果
     */
    public boolean cleanImage(File from, File to) {
        try {
            FileInputStream fis = new FileInputStream(from);
            return cleanImage(fis, to);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 图片过滤处理
     *
     * @param from             要处理的图片文件
     * @param to               图片输出文件
     * @param imageWidthRatio  直接指定图片宽度度形变比例，某些图片形变后识别率可以提高
     * @param imageHeightRatio 直接指定图片高度形变比例，某些图片形变后识别率可以提高
     * @return 处理结果
     */
    public boolean cleanImage(File from, File to, double imageWidthRatio,
                              double imageHeightRatio) {
        try {
            FileInputStream fis = new FileInputStream(from);
            return cleanImage(fis, to, imageWidthRatio, imageHeightRatio);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 图片过滤处理
     *
     * @param from             要处理的图片文件
     * @param to               图片输出文件
     * @param imageWidthRatio  直接指定图片宽度度形变比例，某些图片形变后识别率可以提高
     * @param imageHeightRatio 直接指定图片高度形变比例，某些图片形变后识别率可以提高
     * @param degrees          图片旋转角度
     * @return 处理结果
     */
    public boolean cleanImage(File from, File to, double imageWidthRatio,
                              double imageHeightRatio, int degrees) {
        try {
            FileInputStream fis = new FileInputStream(from);
            return cleanImage(fis, to, imageWidthRatio, imageHeightRatio,
                    degrees);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 图片过滤处理
     *
     * @param from    要处理的图片文件
     * @param to      图片输出文件
     * @param degrees 图片旋转角度
     * @return 处理结果
     */
    public boolean cleanImage(File from, File to, int degrees) {
        try {
            FileInputStream fis = new FileInputStream(from);
            return cleanImage(fis, to, degrees);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 图片过滤处理
     *
     * @param from 要处理的图片文件
     * @param to   图片输出文件
     * @return 处理结果
     */
    public boolean cleanImage(File from, String to) {
        try {
            FileInputStream fis = new FileInputStream(from);
            File out = new File(to);
            return cleanImage(fis, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 图片过滤处理
     *
     * @param from             要处理的图片文件
     * @param to               图片输出文件 直接指定图片宽度度形变比例，某些图片形变后识别率可以提高
     * @param imageWidthRatio  直接指定图片宽度度形变比例，某些图片形变后识别率可以提高
     * @param imageHeightRatio 直接指定图片高度形变比例，某些图片形变后识别率可以提高
     * @return 处理结果
     */
    public boolean cleanImage(File from, String to, double imageWidthRatio,
                              double imageHeightRatio) {
        try {
            FileInputStream fis = new FileInputStream(from);
            File out = new File(to);
            return cleanImage(fis, out, imageWidthRatio, imageHeightRatio);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 图片过滤处理
     *
     * @param from             要处理的图片文件
     * @param to               图片输出文件 直接指定图片宽度度形变比例，某些图片形变后识别率可以提高
     * @param imageWidthRatio  直接指定图片宽度度形变比例，某些图片形变后识别率可以提高
     * @param imageHeightRatio 直接指定图片高度形变比例，某些图片形变后识别率可以提高
     * @param degrees          图片旋转角度
     * @return 处理结果
     */
    public boolean cleanImage(File from, String to, double imageWidthRatio,
                              double imageHeightRatio, int degrees) {
        try {
            FileInputStream fis = new FileInputStream(from);
            File out = new File(to);
            return cleanImage(fis, out, imageWidthRatio, imageHeightRatio,
                    degrees);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 图片过滤处理
     *
     * @param from    要处理的图片文件
     * @param to      图片输出文件
     * @param degrees 图片旋转角度
     * @return 处理结果
     */
    public boolean cleanImage(File from, String to, int degrees) {
        try {
            FileInputStream fis = new FileInputStream(from);
            File out = new File(to);
            return cleanImage(fis, out, degrees);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 图片过滤处理
     *
     * @param from 要处理的图片文件输入流
     * @param to   图片输出文件
     * @return 处理结果
     */
    public boolean cleanImage(InputStream from, File to) {
        return this.cleanImage(from, to, this.imageWidthRatio,
                this.imageHeightRatio, this.degrees);
    }

    /**
     * 图片过滤处理
     *
     * @param from             要处理的图片文件输入流
     * @param to               图片输出文件 直接指定图片宽度度形变比例，某些图片形变后识别率可以提高
     * @param imageWidthRatio  直接指定图片宽度度形变比例，某些图片形变后识别率可以提高
     * @param imageHeightRatio 直接指定图片高度形变比例，某些图片形变后识别率可以提高
     * @return 处理结果
     */
    public boolean cleanImage(InputStream from, File to,
                              double imageWidthRatio, double imageHeightRatio) {
        return cleanImage(from, to, imageWidthRatio, imageHeightRatio,
                this.degrees);
    }

    /**
     * 图片过滤处理
     *
     * @param from             要处理的图片文件输入流
     * @param to               图片输出文件
     * @param imageWidthRatio  直接指定图片宽度度形变比例，某些图片形变后识别率可以提高
     * @param imageHeightRatio 直接指定图片高度形变比例，某些图片形变后识别率可以提高
     * @param degrees          图片旋转角度
     * @return 处理结果
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public boolean cleanImage(InputStream from, File to,
                              double imageWidthRatio, double imageHeightRatio, int degrees) {
        try {
            this.image = ImageIO.read(from);

            this.iw = image.getWidth();
            this.ih = image.getHeight();

            /*
             * 1.旋转场景：图像按顺时针旋转
             */
            this.changeRotate(360);

            this.pixels = new int[iw * ih];

            /*
             * 2，清理场景：利用反射进行清理
             */
            if (this.imageType != Type.NONE) {
                // 获得清理方法列表
                String[] cleanMethods = this.imageType.getClass().getMethod("getCleanMethods").invoke(this.imageType).toString().trim().split("[#,]");

                Class<? extends ImageClean> imageCleanClass = this.getClass();

                for (String cleanMethodName : cleanMethods) {
                    try {
                        cleanMethodName = cleanMethodName.trim();
                        if (cleanMethodName.endsWith("()")) {
                            cleanMethodName = cleanMethodName.replace("()", "");
                        }

                        if (!"".equals(cleanMethodName)) {
                            // 查看缓存
                            Method m = cleanMethodsCache.get(cleanMethodName);
                            if (m != null) {
                                // 自身方法调用
                                m.invoke(cleanMethodsInstanceCache.get(cleanMethodName));
                            } else {
                                Object o = this;
                                //调用指定类
                                if (cleanMethodName.contains(".")) {
                                    String className = cleanMethodName.substring(0, cleanMethodName.lastIndexOf("."));
                                    String methodName = cleanMethodName.substring(cleanMethodName.lastIndexOf(".") + 1);

                                    Class c = Class.forName(className);
                                    // 获得自身方法
                                    m = c.getDeclaredMethod(methodName);

                                    if (instanceCache.get(className) != null) {
                                        o = instanceCache.get(className);
                                    } else {
                                        o = c.getDeclaredConstructor().newInstance();
                                        instanceCache.put(className, o);
                                    }
                                } else { //调用ImageClean
                                    m = imageCleanClass.getDeclaredMethod(cleanMethodName);
                                }

                                m.setAccessible(true);
                                m.invoke(o);
                                cleanMethodsCache.put(cleanMethodName, m);
                                cleanMethodsInstanceCache.put(cleanMethodName, o);
                            }
                        }
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException |
                             SecurityException | ClassNotFoundException | InstantiationException e) {
                        e.printStackTrace();
                    }
                }
            }
            /*
             * 3.形变场景：某些图片形变后识别率可以提高
             */
            // 图片比例调整
            this.changeScaled(imageWidthRatio, imageHeightRatio);

            // image = this.getImage();
            // String pname = args[0].substring(0, args[0].lastIndexOf("."));
            ImageIO.write(image, "png", to);
            return true;
        } catch (IOException | SecurityException | IllegalAccessException | IllegalArgumentException |
                 InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 图片过滤处理
     *
     * @param from    要处理的图片文件输入流
     * @param to      图片输出文件
     * @param degrees 图片旋转角度
     * @return 处理结果
     */
    public boolean cleanImage(InputStream from, File to, int degrees) {
        return this.cleanImage(from, to, this.imageWidthRatio,
                this.imageHeightRatio, degrees);
    }

    /**
     * 图片过滤处理
     *
     * @param from 要处理的图片文件输入流
     * @param to   图片输出文件
     * @return 处理结果
     */
    public boolean cleanImage(InputStream from, String to) {
        return this.cleanImage(from, new File(to));
    }

    /**
     * 图片过滤处理
     *
     * @param from             要处理的图片文件输入流
     * @param to               图片输出文件 直接指定图片宽度度形变比例，某些图片形变后识别率可以提高
     * @param imageWidthRatio  直接指定图片宽度度形变比例，某些图片形变后识别率可以提高
     * @param imageHeightRatio 直接指定图片高度形变比例，某些图片形变后识别率可以提高
     * @return 处理结果
     */
    public boolean cleanImage(InputStream from, String to,
                              double imageWidthRatio, double imageHeightRatio) {
        return cleanImage(from, new File(to), imageWidthRatio, imageHeightRatio);
    }

    /**
     * 图片过滤处理
     *
     * @param from             要处理的图片文件输入流
     * @param to               图片输出文件
     * @param imageWidthRatio  直接指定图片宽度度形变比例，某些图片形变后识别率可以提高
     * @param imageHeightRatio 直接指定图片高度形变比例，某些图片形变后识别率可以提高
     * @param degrees          图片旋转角度
     * @return 处理结果
     */
    public boolean cleanImage(InputStream from, String to,
                              double imageWidthRatio, double imageHeightRatio, int degrees) {

        return this.cleanImage(from, new File(to), imageWidthRatio,
                imageHeightRatio, degrees);
    }

    /**
     * 图片过滤处理
     *
     * @param from    要处理的图片文件输入流
     * @param to      图片输出文件
     * @param degrees 图片旋转角度
     * @return 处理结果
     */
    public boolean cleanImage(InputStream from, String to, int degrees) {
        return this.cleanImage(from, new File(to), degrees);
    }

    /**
     * 图片过滤处理
     *
     * @param from 要处理的图片文件，支持http://、ftp://开头的URL字符串
     * @param to   图片输出文件
     * @return 处理结果
     */
    public boolean cleanImage(String from, File to) {

        try {
            InputStream is;
            if (from.startsWith("http://") || from.startsWith("ftp://")) {
                URL url = new URL(from);
                is = url.openStream();
            } else {
                is = Files.newInputStream(Paths.get(from));
            }
            return cleanImage(is, to);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 图片过滤处理
     *
     * @param from             要处理的图片文件，支持http://、ftp://开头的URL字符串
     * @param to               图片输出文件
     * @param imageWidthRatio  直接指定图片宽度度形变比例，某些图片形变后识别率可以提高
     * @param imageHeightRatio 直接指定图片高度形变比例，某些图片形变后识别率可以提高
     * @return 处理结果
     */
    public boolean cleanImage(String from, File to, double imageWidthRatio,
                              double imageHeightRatio) {
        try {
            InputStream is;
            if (from.startsWith("http://") || from.startsWith("ftp://")) {
                URL url = new URL(from);
                is = url.openStream();
            } else {
                is = Files.newInputStream(Paths.get(from));
            }
            return cleanImage(is, to, imageWidthRatio, imageHeightRatio);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 图片过滤处理
     *
     * @param from             要处理的图片文件，支持http://、ftp://开头的URL字符串
     * @param to               图片输出文件
     * @param imageWidthRatio  直接指定图片宽度度形变比例，某些图片形变后识别率可以提高
     * @param imageHeightRatio 直接指定图片高度形变比例，某些图片形变后识别率可以提高
     * @param degrees          图片旋转角度
     * @return 处理结果
     */
    public boolean cleanImage(String from, File to, double imageWidthRatio,
                              double imageHeightRatio, int degrees) {
        try {
            InputStream is;
            if (from.startsWith("http://") || from.startsWith("ftp://")) {
                URL url = new URL(from);
                is = url.openStream();
            } else {
                is = Files.newInputStream(Paths.get(from));
            }
            return cleanImage(is, to, imageWidthRatio, imageHeightRatio,
                    degrees);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 图片过滤处理
     *
     * @param from    要处理的图片文件，支持http://、ftp://开头的URL字符串
     * @param to      图片输出文件
     * @param degrees 图片顺时针旋转角度
     * @return 处理结果
     */
    public boolean cleanImage(String from, File to, int degrees) {
        try {
            InputStream is;
            if (from.startsWith("http://") || from.startsWith("ftp://")) {
                URL url = new URL(from);
                is = url.openStream();
            } else {
                is = Files.newInputStream(Paths.get(from));
            }
            return cleanImage(is, to, degrees);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 图片过滤处理
     *
     * @param from 要处理的图片文件，支持http://、ftp://开头的URL字符串
     * @param to   图片输出文件
     * @return 处理结果
     */
    public boolean cleanImage(String from, String to) {
        try {
            InputStream is;
            if (from.startsWith("http://") || from.startsWith("ftp://")) {
                URL url = new URL(from);
                is = url.openStream();
            } else {
                is = Files.newInputStream(Paths.get(from));
            }
            File out = new File(to);
            return cleanImage(is, out);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 图片过滤处理
     *
     * @param from             要处理的图片文件，支持http://、ftp://开头的URL字符串
     * @param to               图片输出文件
     * @param imageWidthRatio  直接指定图片宽度度形变比例，某些图片形变后识别率可以提高
     * @param imageHeightRatio 直接指定图片高度形变比例，某些图片形变后识别率可以提高
     * @return 处理结果
     */
    public boolean cleanImage(String from, String to, double imageWidthRatio,
                              double imageHeightRatio) {
        try {
            InputStream is;
            if (from.startsWith("http://") || from.startsWith("ftp://")) {
                URL url = new URL(from);
                is = url.openStream();
            } else {
                is = Files.newInputStream(Paths.get(from));
            }
            File out = new File(to);
            return cleanImage(is, out, imageWidthRatio, imageHeightRatio);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 图片过滤处理
     *
     * @param from             要处理的图片文件，支持http://、ftp://开头的URL字符串
     * @param to               图片输出文件
     * @param imageWidthRatio  直接指定图片宽度度形变比例，某些图片形变后识别率可以提高
     * @param imageHeightRatio 直接指定图片高度形变比例，某些图片形变后识别率可以提高
     * @param degrees          图片旋转角度
     * @return 处理结果
     */
    public boolean cleanImage(String from, String to, double imageWidthRatio,
                              double imageHeightRatio, int degrees) {
        try {
            InputStream is;
            if (from.startsWith("http://") || from.startsWith("ftp://")) {
                URL url = new URL(from);
                is = url.openStream();
            } else {
                is = Files.newInputStream(Paths.get(from));
            }
            File out = new File(to);
            return cleanImage(is, out, imageWidthRatio, imageHeightRatio,
                    degrees);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 图片过滤处理
     *
     * @param from    要处理的图片文件，支持http://、ftp://开头的URL字符串
     * @param to      图片输出文件
     * @param degrees 图片旋转角度
     * @return 处理结果
     */
    public boolean cleanImage(String from, String to, int degrees) {
        try {
            InputStream is;
            if (from.startsWith("http://") || from.startsWith("ftp://")) {
                URL url = new URL(from);
                is = url.openStream();
            } else {
                is = Files.newInputStream(Paths.get(from));
            }
            File out = new File(to);
            return cleanImage(is, out, degrees);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 将色彩根据周围比重较重的颜色转为黑或白
     *
     * @return 转换后的BufferedImage
     */
    protected BufferedImage jugementBlackOrWhite() {
        int[][] pixelsArray = new int[ih][iw];
        int minx = image.getMinX();
        int miny = image.getMinY();
        // 初始化RGB数组
        for (int i = miny; i < ih; i++) {
            for (int j = minx; j < iw; j++) {
                pixelsArray[i][j] = image.getRGB(j, i);
            }
        }

        // 修改像素数组，清除边框
        for (int r = 0; r < ih; r++) {
            for (int c = 0; c < iw; c++) {
                //不是黑色或白色，则根据周围颜色比重确定当前点颜色
                if (pixelsArray[r][c] != -1 && pixelsArray[r][c] != -16777216) {
                    //true 白色   -1
                    if (checkBlackOrWhite(pixelsArray, r, c, iw, ih)) {
                        pixelsArray[r][c] = -1;
                    } else { //false 黑色 -16777216
                        pixelsArray[r][c] = -16777216;
                    }
                }
            }
        }

        // 修改图像
        for (int i = miny; i < ih; i++) {
            for (int j = minx; j < iw; j++) {
                image.setRGB(j, i, pixelsArray[i][j]);
            }
        }
        return image;
    }

    /**
     * 检测颜色的黑白类型
     *
     * @param pixelsArray 像素数组
     * @param r           行
     * @param c           列
     * @param w           宽
     * @param h           高
     * @return true白色，false黑色
     */
    protected boolean checkBlackOrWhite(int[][] pixelsArray, int r,
                                        int c, int w, int h) {
        boolean flag = true;

        int white = 0; //白色个数  -1
        int black = 0; //黑色个数 -16777216

        //上下左右
        int rUp = r - 1;

        if (checkPixelRange(rUp, c, w, h)) {
            if (pixelsArray[rUp][c] == -1) { //如果不是孤儿点
                white++;
            } else if (pixelsArray[rUp][c] == -16777216) {
                black++;
            }
        }

        int rDown = r + 1;

        if (checkPixelRange(rDown, c, w, h)) {
            if (pixelsArray[rDown][c] == -1) { //如果不是孤儿点
                white++;
            } else if (pixelsArray[rDown][c] == -16777216) {
                black++;
            }
        }

        int cLeft = c - 1;

        if (checkPixelRange(r, cLeft, w, h)) {
            if (pixelsArray[r][cLeft] == -1) { //如果不是孤儿点
                white++;
            } else if (pixelsArray[r][cLeft] == -16777216) {
                black++;
            }
        }

        int cRight = c + 1;

        if (checkPixelRange(r, cRight, w, h)) {
            if (pixelsArray[r][cRight] == -1) { //如果不是孤儿点
                white++;
            } else if (pixelsArray[r][cRight] == -16777216) {
                black++;
            }
        }
        //右上，右下，左上，左下
        int rRightUp = r - 1;
        int cRightUp = c + 1;
        int rRightDown = r + 1;
        int cRightDown = c + 1;

        int rLeftUp = r - 1;
        int cLeftUp = c - 1;
        int rLeftDown = r + 1;
        int cLeftDown = c - 1;

        if (checkPixelRange(rRightUp, cRightUp, w, h)) {
            if (pixelsArray[rRightUp][cRightUp] == -1) { //如果不是孤儿点
                white++;
            } else if (pixelsArray[rRightUp][cRightUp] == -16777216) {
                black++;
            }
        }
        if (checkPixelRange(rRightDown, cRightDown, w, h)) {
            if (pixelsArray[rRightDown][cRightDown] == -1) { //如果不是孤儿点
                white++;
            } else if (pixelsArray[rRightDown][cRightDown] == -16777216) {
                black++;
            }
        }
        if (checkPixelRange(rLeftUp, cLeftUp, w, h)) {
            if (pixelsArray[rLeftUp][cLeftUp] == -1) { //如果不是孤儿点
                white++;
            } else if (pixelsArray[rLeftUp][cLeftUp] == -16777216) {
                black++;
            }
        }
        if (checkPixelRange(rLeftDown, cLeftDown, w, h)) {
            if (pixelsArray[rLeftDown][cLeftDown] == -1) { //如果不是孤儿点
                white++;
            } else if (pixelsArray[rLeftDown][cLeftDown] == -16777216) {
                black++;
            }
        }
        if (white > black) {
            flag = true;
        }
        return flag;
    }

    /**
     * 清除十字方向孤儿点的干扰，周围如果小于1个点则清除
     *
     * @return 图像
     */
    protected BufferedImage cleanCrossOrphan() {
        int roundCount = 1;
        return cleanOrphan(roundCount, true);
    }

    /**
     * 清除米字方向孤儿点的干扰，周围如果小于1个点则清除
     *
     * @return 图像
     */
    protected BufferedImage cleanOrphan() {
        int roundCount = 1;
        return cleanOrphan(roundCount, false);
    }

    /**
     * 清除孤儿点的干扰，
     * 清除孤儿点的干扰，周围如果小于roundCount个点则清除
     *
     * @param roundCount 孤儿点判断临界点数，小于roundCount个点则清除
     * @param cross      true，仅检查十字方向；false，检查米字方向
     * @return 图像对象
     */
    protected BufferedImage cleanOrphan(int roundCount, boolean cross) {
        int[][] pixelsArray = new int[ih][iw];
        int minx = image.getMinX();
        int miny = image.getMinY();

        // 初始化RGB数组
        for (int i = miny; i < ih; i++) {
            for (int j = minx; j < iw; j++) {
                pixelsArray[i][j] = image.getRGB(j, i);
            }
        }

        // 修改像素数组，清除边框
        for (int r = 0; r < ih; r++) {
            for (int c = 0; c < iw; c++) {
                if (pixelsArray[r][c] != -1) {
                    if (checkAround(pixelsArray, r, c, iw, ih, cross) <= roundCount) { //孤儿点
                        pixelsArray[r][c] = -1;
                    }
                }
            }
        }

        // 修改图像
        for (int i = miny; i < ih; i++) {
            for (int j = minx; j < iw; j++) {
                image.setRGB(j, i, pixelsArray[i][j]);
            }
        }
        return image;
    }


    /**
     * 检测当前点周围其他点的个数，用来判断是否是孤儿点
     *
     * @param pixelsArray 像素数组
     * @param r           当前行
     * @param c           当前列
     * @param w           像素宽度
     * @param h           像素高度
     * @param cross       true，仅检查十字方向；false，检查米字方向
     * @return 周围存在点的个数（非白色）
     */
    protected int checkAround(int[][] pixelsArray, int r,
                              int c, int w, int h, boolean cross) {
        int res = 0;

        //上下左右
        int rUp = r - 1;

        if (checkPixelRange(rUp, c, w, h)) {
            if (pixelsArray[rUp][c] != -1) { //如果不是孤儿点
                res++;
            }
        }

        int rDown = r + 1;

        if (checkPixelRange(rDown, c, w, h)) {
            if (pixelsArray[rDown][c] != -1) { //如果不是孤儿点
                res++;
            }
        }

        int cLeft = c - 1;

        if (checkPixelRange(r, cLeft, w, h)) {
            if (pixelsArray[r][cLeft] != -1) { //如果不是孤儿点
                res++;
            }
        }

        int cRight = c + 1;

        if (checkPixelRange(r, cRight, w, h)) {
            if (pixelsArray[r][cRight] != -1) { //如果不是孤儿点
                res++;
            }
        }

        //米字检测
        if (!cross) {
            //右上，右下，左上，左下
            int rRightUp = r - 1;
            int cRightUp = c + 1;
            int rRightDown = r + 1;
            int cRightDown = c + 1;

            int rLeftUp = r - 1;
            int cLeftUp = c - 1;
            int rLeftDown = r + 1;
            int cLeftDown = c - 1;

            if (checkPixelRange(rRightUp, cRightUp, w, h)) {
                if (pixelsArray[rRightUp][cRightUp] != -1) { //如果不是孤儿点
                    res++;
                }
            }
            if (checkPixelRange(rRightDown, cRightDown, w, h)) {
                if (pixelsArray[rRightDown][cRightDown] != -1) { //如果不是孤儿点
                    res++;
                }
            }
            if (checkPixelRange(rLeftUp, cLeftUp, w, h)) {
                if (pixelsArray[rLeftUp][cLeftUp] != -1) { //如果不是孤儿点
                    res++;
                }
            }
            if (checkPixelRange(rLeftDown, cLeftDown, w, h)) {
                if (pixelsArray[rLeftDown][cLeftDown] != -1) { //如果不是孤儿点
                    res++;
                }
            }
        }
        return res;
    }

    /**
     * 清除文字周围的填充，主要用于空心文字处理时的文字提取
     *
     * @return 转换后的BufferedImage
     */
    protected BufferedImage cleanPadding() {
        int[][] pixelsArray = new int[ih][iw];
        int minx = image.getMinX();
        int miny = image.getMinY();
        // 初始化RGB数组
        for (int i = miny; i < ih; i++) {
            for (int j = minx; j < iw; j++) {
                pixelsArray[i][j] = image.getRGB(j, i);
            }
        }

        // 修改像素数组，清除边框
        Hight:
        for (int r = 0; r < ih; r++) {
            for (int c = 0; c < iw; c++) {
                if (pixelsArray[r][c] != -1) {
                    pixelsArray[r][c] = -1;
                    searchAdjacentRecursion(pixelsArray, r, c, iw, ih);
                    break Hight;
                }
            }
        }

        // 修改图像
        for (int i = miny; i < ih; i++) {
            for (int j = minx; j < iw; j++) {
                image.setRGB(j, i, pixelsArray[i][j]);
            }
        }
        return image;
    }

    /**
     * 将图像转为黑白
     *
     * @return 图片对象
     */
    protected BufferedImage convertImageToGrayscale() {
        BufferedImage tmp = new BufferedImage(image.getWidth(),
                image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g2 = tmp.createGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
        return image = tmp;
    }

    /**
     * 获得图片的平均灰度
     *
     * @param width  图片宽度
     * @param height 图片高度
     * @return 图片对象
     */
    protected int getAvgValue(int width, int height) {
        Color point;
        int total = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                point = new Color(image.getRGB(j, i));
                total += (point.getRed() + point.getGreen() + point.getBlue()) / 3;
            }
        }
        return total / (width * height);
    }

    /**
     * 获得黑色
     *
     * @return 黑色
     */
    protected int getBlackPoint() {
        return (new Color(0, 0, 0).getRGB());
    }

    /**
     * Blur by "convolving" the image with a matrix
     *
     * @return 图片对象
     */
    protected BufferedImage getBlur() {
        float[] data = {.1111f, .1111f, .1111f, .1111f, .1111f, .1111f,
                .1111f, .1111f, .1111f,};
        ConvolveOp cop = new ConvolveOp(new Kernel(3, 3, data));
        return image = cop.filter(image, null);

    }

    /**
     * 获得图片顺时针旋转角度
     *
     * @return 图片顺时针旋转角度
     */
    public int getDegrees() {
        return degrees;
    }

    /**
     * 获得处理后的图片对象
     *
     * @return 图片对象
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * 获得图片高度形变比例
     *
     * @return 图片高度形变比例
     */
    public double getImageHeightRatio() {
        return imageHeightRatio;
    }

    /**
     * 获得图片类型
     *
     * @return 图片类型
     */
    public Type getImageType() {
        return imageType;
    }

    /**
     * 获得图片宽度形变比例
     *
     * @return 图片宽度形变比例
     */
    public double getImageWidthRatio() {
        return imageWidthRatio;
    }

    /**
     * Sharpen by using a different matrix
     *
     * @return 图片对象
     */
    protected BufferedImage getSharpen() {
        float[] data = {0.0f, -0.75f, 0.0f, -0.75f, 4.0f, -0.75f, 0.0f,
                -0.75f, 0.0f};
        ConvolveOp cop = new ConvolveOp(new Kernel(3, 3, data));
        return image = cop.filter(image, null);
    }

    /**
     * 获得白色
     *
     * @return 白色
     */
    protected int getWhitePoint() {
        return (new Color(255, 255, 255).getRGB());
    }

    /**
     * 灰度反转，适合白色文字，纯色背景：
     */
    protected void reverseGray() {

        for (int y = 0; y < ih; y++) {
            for (int x = 0; x < iw; x++) {
                int rgb = image.getRGB(x, y);
                Color color = new Color(rgb); // 根据rgb的int值分别取得r,g,b颜色。
                Color newColor = new Color(255 - color.getRed(),
                        255 - color.getGreen(), 255 - color.getBlue());
                image.setRGB(x, y, newColor.getRGB());
            }
        }
    }

    /**
     * 设置图片顺时针旋转角度
     *
     * @param degrees 图片顺时针旋转角度
     */
    public void setDegrees(int degrees) {
        this.degrees = degrees;
    }

    /**
     * 图片高度形变比例，某些图片形变后识别率可以提高，1为原始比例，不形变
     *
     * @param imageHeightRatio 图片宽度形变比例，某些图片形变后识别率可以提高，默认值1为原始比例，不形变
     */
    public void setImageHeightRatio(double imageHeightRatio) {
        this.imageHeightRatio = imageHeightRatio;
    }

    /**
     * 设置图片宽度和高度形变比例，某些图片形变后识别率可以提高，1为原始比例，不形变
     *
     * @param imageWidthRatio  图片宽度形变比例，某些图片形变后识别率可以提高，1为原始比例，不形变
     * @param imageHeightRatio 图片高度形变比例，某些图片形变后识别率可以提高，1为原始比例，不形变
     */
    public void setImageRatio(double imageWidthRatio, double imageHeightRatio) {
        this.imageWidthRatio = imageWidthRatio;
        this.imageHeightRatio = imageHeightRatio;
    }

    /**
     * 设置图片类型
     *
     * @param imageType 过滤等级：IMAGE_TYPE_CAPTCHA_NORMAL,...
     */
    public void setImageType(Type imageType) {
        this.imageType = imageType;
    }

    /**
     * 图片宽度形变比例，某些图片形变后识别率可以提高，1为原始比例，不形变
     *
     * @param imageWidthRatio 图片宽度形变比例，某些图片形变后识别率可以提高，默认值1为原始比例，不形变
     */
    public void setImageWidthRatio(double imageWidthRatio) {
        this.imageWidthRatio = imageWidthRatio;
    }



    /**
     * 验证码图片过滤清理类型枚举，必须实现Type接口 <br>
     * 可选择7种验证码图片清理类型： <br>
     * - 普通验证码图片 <br>
     * - 带干扰线的图片 <br>
     * - 点状验证码图片 <br>
     * - 白色文字,纯色背景验证码图片 <br>
     * - 空心文字验证码图片 <br>
     * - 无特殊干扰的普通图片清晰化 <br>
     * - 不做任何处理 <br>
     *
     * @author Ray
     * @author inthinkcolor@gmail.com
     * @author easyproject.cn
     * @version 3.0.3
     *
     */
    public enum Type {
        /**
         * 图片类型：普通验证码图片
         */
        CAPTCHA_NORMAL("changeToGrey" + "#changeToBinarization"),
        /**
         * 图片类型：带干扰线的图片
         */
        CAPTCHA_INTERFERENCE_LINE("#changeToBinarization" + "#cleanCrossOrphan"
                + "#cleanCrossOrphan" + "#changeToBinarization"
                + "#changeToGrayByAvgColor" + "#changeToBlackWhiteImage"),
        /**
         * 图片类型：点状验证码图片
         */
        CAPTCHA_SPOT("changeToGrayByAvgColor" + "#changeBrighten"
                + "#changeToBlackWhiteImage"),
        /**
         * 图片类型：白色文字，纯色背景验证码图片
         */
        CAPTCHA_WHITE_CHAR("reverseGray" + "#changeToGrey"
                + "#changeToBinarization"),
        /**
         * 图片类型：空心文字验证码图片
         */
        // 空心处理1.转换为包围字
        // 空心处理2.去掉包围，得到文字图片
        // 空心处理3.文字
        // 空心处理4.转为白底黑子
        CAPTCHA_HOLLOW_CHAR("getBlur" + "#changeToBlackWhiteImage"
                + "#changeBrighten" + "#reverseGray" + "," + "cleanPadding" + ","
                + "getBlur" + "#changeToBlackWhiteImage" + "#reverseGray"
                + "#changeBrighten" + "," + "reverseGray"),
        /**
         * 无特殊干扰的普通图片清晰化
         */
        CLEAR("convertImageToGrayscale"),
        /**
         * 不做任何处理
         */
        NONE(""),
        SCORE("#,,###changeBrighten,changeToBinarization#"),
        TEAM("#,,##reverseGray"
                + "#,###changeBrighten#"
                + "changeToBinarization#"
                + "reverseGray#"
                + "changeBrighten#"
                + "reverseGray#"
                + "changeToGrayByAvgColor#"
                + "#changeToGrey#"),
        PLAYER("#,"
//			+ ",##changeToGrey#"
//			+ ",##reverseGray#"
//			+ "#,###changeBrighten#"
//			+ "changeToBinarization#"
//			+ "reverseGray#"
//			+ "changeBrighten#"
//			+ "reverseGray#"
//			+ "changeToGrayByAvgColor#"
                + "#changeToGrey#"
                + "changeToBinarization#"
//			+ "#changeToBlackWhiteImage#"
        );

        private final String cleanMethods;

        Type(String cleanMethods) {
            this.cleanMethods = cleanMethods;
        }

        /**
         * 返回方法调用结果
         * @return 清理方法列表
         */
        public String getCleanMethods() {
            return this.cleanMethods;
        }

    }

}
