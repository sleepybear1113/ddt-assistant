package cn.xiejx.ddtassistant.type.auction;

import cn.xiejx.ddtassistant.constant.Constants;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/06/03 22:48
 */
public class AuctionConstants {
    public static final String FILENAME = Constants.CONFIG_DIR + "action-list-config.json";

    /**
     * 拍卖物品 tab 横向
     */
    public static final int[] SELL_BANNER_POINT = {160, 70};

    /**
     * 拍卖场物品框坐标
     */
    public static final int[] AUCTION_INPUT_POINT = {70, 190};

    /**
     * 竞拍价坐标，偏右
     */
    public static final int[] ARGUE_PRICE_POINT = {160, 260};

    /**
     * 一口价坐标，偏右
     */
    public static final int[] MOUTHFUL_PRICE_POINT = {160, 320};

    /**
     * 物品数量输入框的数字框坐标
     */
    public static final int[] NUM_INPUT_BOX_NUM_POINT = {500, 280};

    /**
     * 输入数量框的<strong>确认</strong>按钮位置坐标
     */
    public static final int[] NUM_INPUT_BOX_CONFIRM_POINT = {420, 370};

    /**
     * 输入数量框的<strong>取消</strong>按钮位置坐标
     */
    public static final int[] NUM_INPUT_BOX_CANCEL_POINT = {580, 370};

    /**
     * 装备栏 tab 坐标
     */
    public static final int[] EQUIPMENT_TAB_POINT = {680, 180};

    /**
     * 道具栏 tab 坐标
     */
    public static final int[] PROPS_TAB_POINT = {680, 310};

    /**
     * 进行拍卖的按钮位置
     */
    public static final int[] SELL_POINT = {140, 490};
    /**
     * 放回物品的坐标
     */
    public static final int[] PUT_BACK_POINT = {100, 130};

    /**
     * 弹框输入物品数量的整个区域。如果一个物品有多于一个数量，那么会弹框
     */
    public static final int[] NUM_INPUT_BOX_RECT = {};

    /**
     * 输入数量弹窗的“拍卖数量”样图范围
     */
    public static final int[] NUM_INPUT_BOX_SELL_NUM_SAMPLE_RECT = {370, 268, 450, 297};

    /**
     * 与 NUM_INPUT_BOX_SELL_NUM_SAMPLE_RECT 对应，查找区域
     */
    public static final int[] NUM_INPUT_BOX_FIND_SELL_RECT = {360, 240, 460, 300};

    /**
     * 输入数量框的 ocr 范围
     */
    public static final int[] NUM_INPUT_OCR_RECT = {458, 274, 510, 290};

    /**
     * 物品放置区域 ocr 范围
     */
    public static final int[] ITEM_INPUT_NAME_OCR_RECT = {92, 165, 238, 192};

    /**
     * 拍卖场标题三个字的区域模板
     */
    public static final int[] AUCTION_TITLE_SAMPLE_RECT = {};

    /**
     * 拍卖场标题三个字的区域的寻找范围
     */
    public static final int[] AUCTION_TITLE_FIND_RECT = {};

    public enum AuctionTimeEnum {
        /**
         * 8 24 48 小时的拍卖时限坐标
         */
        HOUR_8("8", new int[]{80, 380}),
        HOUR_24("24", new int[]{140, 380}),
        HOUR_48("48", new int[]{210, 380}),
        ;

        private final int[] position;
        private final String time;

        AuctionTimeEnum(String time, int[] position) {
            this.time = time;
            this.position = position;
        }

        public int[] getPosition() {
            return position;
        }

        public String getTime() {
            return time;
        }

        public static AuctionTimeEnum getAuctionTime(String s) {
            for (AuctionTimeEnum value : values()) {
                if (value.getTime().equals(s)) {
                    return value;
                }
            }
            return HOUR_48;
        }
    }

    public static class AuctionPosition {
        public static final int[] FIRST_POINT = {345, 155};
        public static final int GAP = 45;
        public static final int N = 7;

        public static int[] getPoint(int x, int y) {
            if (x <= 0 || y <= 0) {
                return null;
            }
            if (x > N || y > N) {
                return null;
            }

            int xx = FIRST_POINT[0] + GAP * (y - 1);
            int yy = FIRST_POINT[1] + GAP * (x - 1);
            return new int[]{xx, yy};
        }

        public static int[] getPoint(int n) {
            if (n <= 0 || n > N * N) {
                return null;
            }

            int x = (n - 1) / N + 1;
            int y = n % N;
            y = y == 0 ? N : y;
            return getPoint(x, y);
        }
    }

}
