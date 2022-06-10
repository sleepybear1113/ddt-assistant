package cn.xiejx.ddtassistant.type.common;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/06/10 11:56
 */
public class CommonConstants {

    /**
     * 游戏右下角“更多”的坐标
     */
    public static final int[] MORE_POINT = {};

    /**
     * 游戏右下角“更多”的模板图片区域
     */
    public static final int[] MORE_SAMPLE_RECT = {};

    /**
     * 游戏右下角“更多”的寻找区域
     */
    public static final int[] MORE_FIND_RECT = {};

    /**
     * 游戏右下角“更多”是否打开的模板图片区域
     */
    public static final int[] MORE_OPEN_SAMPLE_RECT = {};

    /**
     * 游戏右下角“更多”是否打开的寻找区域
     */
    public static final int[] MORE_OPEN_FIND_RECT = {};

    public enum MoreTypeEnum {
        /**
         * 枚举
         */
        AUCTION(new int[]{1, 1}, "拍卖场"),
        GAME_CENTER(new int[]{1, 1}, "游戏大厅"),
        PVE(new int[]{1, 1}, "远征码头"),
        SETTING(new int[]{1, 1}, "设置"),
        GUILD_CLUB(new int[]{1, 1}, "公会俱乐部"),
        ;

        private final int[] point;
        private final String type;

        MoreTypeEnum(int[] point, String type) {
            this.point = point;
            this.type = type;
        }

        public int[] getPoint() {
            return point;
        }

        public String getType() {
            return type;
        }
    }

}
