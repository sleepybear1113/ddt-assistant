package cn.xiejx.ddtassistant.constant;

/**
 * @author sleepybear
 */
public class Constants {
    /**
     * ==============================================
     * <p>一级文件夹</p>
     * ==============================================
     */
    public static final String RESOURCE_PIC_DIR = "资源图片/";
    public static final String RESOURCE_FILE_DIR = "资源文件/";
    public static final String PIC_DIR = "图片/";
    public static final String CONFIG_DIR = "用户配置文件/";
    public static final String TEMP_DIR = "临时文件/";

    /**
     * ==============================================
     * <p>二级文件夹</p>
     * ==============================================
     */
    public static final String TEMPLATE_PICTURE_DIR = RESOURCE_PIC_DIR + "模板/";
    public static final String FLOP_BONUS_DIR = PIC_DIR + "副本翻牌截图/";
    public static final String CAPTCHA_DIR = PIC_DIR + "验证码截图/";
    public static final String TESS_DATA_DIR = "tessdata";
    public static final String TEMP_GAME_SCREEN_SHOT_DIR = TEMP_DIR + "gameScreen/";
    public static final String AUCTION_TMP_DIR = Constants.TEMP_DIR + "auction/";
    public static final String DESKTOP_SCREEN_SHOT_DIR = Constants.TEMP_DIR + "screenshot/";

    /**
     * ==============================================
     * <p>三级文件夹</p>
     * ==============================================
     */
    public static final String CAPTCHA_COUNT_DOWN_DIR = CAPTCHA_DIR + "倒计时截图/";

    /*
     * ====================================================================
     * ====================================================================
     */
    public static final String BMP_SUFFIX = ".bmp";
    public static final String JPG_SUFFIX = ".jpg";
    public static final String PNG_SUFFIX = ".png";

    public static String[] firstDirList() {
        return new String[]{Constants.RESOURCE_PIC_DIR, Constants.RESOURCE_FILE_DIR, Constants.PIC_DIR, Constants.CONFIG_DIR, Constants.TEMP_DIR};
    }
}
