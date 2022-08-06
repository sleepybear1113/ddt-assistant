package cn.xiejx.ddtassistant.type.auction;

import cn.xiejx.ddtassistant.constant.Constants;
import cn.xiejx.ddtassistant.utils.ImgUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/06/03 22:48
 */
public class AuctionConstants {
    public static final String FILENAME = Constants.CONFIG_DIR + "拍卖场配置文件.json";

    /**
     * 背包物品区域
     */
    public static final int[] BAG_AREA_RECT = {326, 132, 640, 450};

    /**
     * 背包每个格子之间的距离像素
     */
    public static final int BAG_GAP = 4;

    /**
     * 背包空白颜色
     */
    public static final int[] EMPTY_SAMPLE_COLOR = {208, 195, 146};

    /**
     * 拍卖物品 tab 横向
     */
    public static final int[] SOLD_OUT_TAB_POINT = {160, 70};

    /**
     * 拍卖场物品框坐标
     */
    public static final int[] AUCTION_INPUT_POINT = {70, 190};

    /**
     * 拍卖场下方空白区域
     */
    public static final int[] BOTTOM_BLANK_AREA_POINT = {140, 570};

    /**
     * 竞拍价坐标，偏右
     */
    public static final int[] ARGUE_PRICE_POINT = {160, 260};

    /**
     * 一口价坐标，偏右
     */
    public static final int[] MOUTHFUL_PRICE_POINT = {160, 320};

    /**
     * 竞拍价 ocr 区域范围
     */
    public static final int[] ARGUE_PRICE_OCR_RECT = {52, 251, 150, 268};

    /**
     * 一口价 ocr 区域范围
     */
    public static final int[] MOUTHFUL_PRICE_OCR_RECT = {52, 312, 150, 329};

    /**
     * 物品数量输入框的数字框坐标
     */
    public static final int[] NUM_INPUT_BOX_NUM_POINT = {520, 280};

    /**
     * 物品输入框加号按钮坐标
     */
    public static final int[] NUM_INPUT_BOX_NUM_ADD_POINT = {614, 274};

    /**
     * 物品输入框减号按钮坐标
     */
    public static final int[] NUM_INPUT_BOX_NUM_DESC_POINT = {614, 289};

    /**
     * 输入数量框的<strong>确认</strong>按钮位置坐标
     */
    public static final int[] NUM_INPUT_BOX_CONFIRM_POINT = {420, 370};

    /**
     * 输入数量框的<strong>取消</strong>按钮位置坐标
     */
    public static final int[] NUM_INPUT_BOX_CANCEL_POINT = {580, 370};

    /**
     * 卖金币确认按钮
     */
    public static final int[] SOLD_OUT_BOX_CONFIRM_POINT = {430, 360};

    /**
     * 卖金币取消按钮
     */
    public static final int[] SOLD_OUT_BOX_CANCEL_POINT = {570, 360};

    /**
     * 装备栏 tab 坐标
     */
    public static final int[] EQUIPMENT_TAB_POINT = {680, 180};

    /**
     * 道具栏 tab 坐标
     */
    public static final int[] PROPS_TAB_POINT = {680, 310};

    /**
     * 装备栏 tab 模板区域
     */
    public static final int[] EQUIPMENT_TAB_TEMPLATE_RECT = {655, 150, 690, 240};

    /**
     * 道具栏 tab 模板区域
     */
    public static final int[] PROPS_TAB_TEMPLATE_RECT = {655, 280, 690, 370};

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
     * 拍卖场丢弃物品，弹出提示框，”是否需要出售“字样区域
     */
    public static final int[] SOLD_OUT_BOX_CONFIRM_SAMPLE_RECT = {435, 285, 547, 307};

    /**
     * 与 NUM_INPUT_BOX_SELL_NUM_SAMPLE_RECT 对应，查找区域
     */
    public static final int[] NUM_INPUT_BOX_FIND_SELL_RECT = {360, 240, 460, 300};

    /**
     * 输入数量框的 ocr 范围
     */
    public static final int[] NUM_INPUT_OCR_RECT = {458, 274, 510, 290};

    /**
     * 输入数量框、和卖金币的确认取消模板区域
     */
    public static final int[] NUM_INPUT_OR_DROP_CONFIRM_CANCEL_SAMPLE_RECT = {380, 345, 630, 385};

    /**
     * 输入数量框、和卖金币的确认取消的寻找区域
     */
    public static final int[] NUM_INPUT_OR_DROP_CONFIRM_CANCEL_FIND_RECT = {300, 180, 700, 420};

    /**
     * 物品放置区域物品图标范围的无物品的标准颜色
     */
    public static final int[] ITEM_INPUT_ICON_STANDARD_COLOR = {58, 58, 58};

    /**
     * 物品放置区域物品图标范围
     */
    public static final int[] ITEM_INPUT_ICON_RECT = {50, 165, 85, 200};

    /**
     * 物品放置区域 ocr 范围
     */
    public static final int[] ITEM_INPUT_NAME_OCR_RECT = {92, 165, 238, 192};

    /**
     * 拍卖场标题三个字的区域模板
     */
    public static final int[] AUCTION_TITLE_SAMPLE_RECT = {430, 10, 550, 40};

    /**
     * 拍卖场标题三个字的区域的寻找范围
     */
    public static final int[] AUCTION_TITLE_FIND_RECT = {};

    /**
     * 物品已经添加进拍卖行 字样的寻找区域
     */
    public static final int[] ADD_TO_AUCTION_MEG_FIND_RECT = {320, 250, 720, 350};


    /**
     * 拍卖场右上角“拍卖说明”字样模板区域
     */
    public static final int[] SOLD_OUT_INTRO_TEMPLATE_RECT = {900, 60, 995, 85};

    /**
     * 拍卖场背包打开模板区域
     */
    public static final int[] BAG_OPEN_TEMPLATE_RECT = {528, 55, 585, 115};

    /**
     * 拍卖场“物品搜索” tab 模板区域
     */
    public static final int[] SEARCH_TAB_TEMPLATE_RECT = {20, 55, 108, 85};

    /**
     * 拍卖场“拍卖物品” tab 模板区域
     */
    public static final int[] SOLD_OUT_TAB_TEMPLATE_RECT = {122, 55, 210, 85};

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
        public static final int GAP = ((BAG_AREA_RECT[2] - BAG_AREA_RECT[0]) - 6 * BAG_GAP) / 7 + BAG_GAP;
        public static final int[] FIRST_POINT = {BAG_AREA_RECT[0] + GAP / 2, BAG_AREA_RECT[1] + GAP / 2};
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

        public static int[] getIndexRect(int n) {
            int[] point = getPoint(n);
            if (point == null) {
                return null;
            }
            return new int[]{point[0] - GAP / 2 - BAG_AREA_RECT[0], point[1] - GAP / 2 - BAG_AREA_RECT[1], point[0] + GAP / 2 - BAG_AREA_RECT[0] - BAG_GAP, point[1] + GAP / 2 - BAG_AREA_RECT[1] - BAG_GAP};
        }

        public static BufferedImage getIndexPic(String path, int n) {
            try {
                BufferedImage img = ImageIO.read(new File(path));
                int[] rect = getIndexRect(n);
                if (rect == null) {
                    return null;
                }
                return img.getSubimage(rect[0], rect[1], rect[2] - rect[0], rect[3] - rect[1]);
            } catch (IOException e) {
                return null;
            }
        }

        public static int[] getIndexPicAvgColor(String path, int n) {
            BufferedImage image = getIndexPic(path, n);
            return ImgUtil.getAvgColor(image);
        }

        public static boolean bagIndexEmpty(String path, int n) {
            BufferedImage image = getIndexPic(path, n);
            int[] avgColor = ImgUtil.getAvgColor(image);
            if (avgColor == null) {
                return false;
            }
            int delta = 20;
            int r = Math.abs(avgColor[0] - EMPTY_SAMPLE_COLOR[0]);
            int g = Math.abs(avgColor[1] - EMPTY_SAMPLE_COLOR[1]);
            int b = Math.abs(avgColor[2] - EMPTY_SAMPLE_COLOR[2]);
            return r < delta && g < delta && b < delta;
        }

        public static List<Integer> getNotEmptyBagIndex(String path) {
            List<Integer> list = new ArrayList<>();
            for (int i = 1; i <= N * N; i++) {
                if (bagIndexEmpty(path, i)) {
                    continue;
                }
                list.add(i);
            }
            return list;
        }

        public enum SellType {
            /**
             * 先道具再装备
             */
            PROPS_AND_EQUIPMENT(1),
            /**
             * 仅装备
             */
            PROPS_ONLY(2),
            /**
             * 先装备再道具
             */
            EQUIPMENT_AND_PROPS(3),
            /**
             * 仅装备
             */
            EQUIPMENT_ONLY(4),
            ;
            private final Integer type;

            SellType(Integer type) {
                this.type = type;
            }

            public Integer getType() {
                return type;
            }

            public static Integer getType(Integer type) {
                return getSellType(type).getType();
            }

            public static SellType getSellType(Integer type) {
                if (type == null) {
                    return PROPS_AND_EQUIPMENT;
                }
                for (SellType sellType : values()) {
                    if (sellType.getType().equals(type)) {
                        return sellType;
                    }
                }
                return PROPS_AND_EQUIPMENT;
            }
        }
    }
}
