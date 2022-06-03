package cn.xiejx.ddtassistant.type.auction;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/06/03 22:48
 */
public class AuctionConstants {

    /**
     * 拍卖物品 tab 横向
     */
    public static final int[] SELL_BANNER_POINT = {160, 70};

    /**
     * 拍卖场物品框坐标
     */
    public static final int[] AUCTION_INPUT_POINT = {140, 170};

    /**
     * 竞拍价坐标，偏右
     */
    public static final int[] ARGUE_PRICE_POINT = {160, 260};

    /**
     * 一口价坐标，偏右
     */
    public static final int[] MOUTHFUL_PRICE_POINT = {160, 310};

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
    public static final int[] SOLD_POINT = {140, 490};

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
    public static final int[] NUM_INPUT_OCR_RECT = {458, 272, 510, 290};

    /**
     * 物品放置区域 ocr 范围
     */
    public static final int[] ITEM_INPUT_NAME_OCR_RECT = {92, 165, 238, 192};

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
}
