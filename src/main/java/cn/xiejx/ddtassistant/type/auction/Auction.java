package cn.xiejx.ddtassistant.type.auction;

import cn.xiejx.ddtassistant.config.AuctionItem;
import cn.xiejx.ddtassistant.config.AuctionList;
import cn.xiejx.ddtassistant.constant.GlobalVariable;
import cn.xiejx.ddtassistant.dm.DmDdt;
import cn.xiejx.ddtassistant.utils.OcrUtil;
import cn.xiejx.ddtassistant.utils.SpringContextUtil;
import cn.xiejx.ddtassistant.utils.Util;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Arrays;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/06/02 20:42
 */
@Slf4j
public class Auction {

    private final DmDdt dm;

    private boolean running;
    private boolean stop;

    private Auction(DmDdt dm) {
        this.dm = dm;
        this.running = false;
        this.stop = false;
    }

    public static boolean isRunning(int hwnd) {
        Auction auction = GlobalVariable.AUCTION_MAP.get(hwnd);
        if (auction == null) {
            return false;
        }
        return auction.running;
    }

    public static Auction createInstance(DmDdt dm) {
        Auction auction = GlobalVariable.AUCTION_MAP.get(dm.getHwnd());
        if (auction != null) {
            return auction;
        }

        auction = new Auction(dm);
        GlobalVariable.AUCTION_MAP.put(dm.getHwnd(), auction);
        return auction;
    }

    public void stop() {
        this.running = false;
        GlobalVariable.AUCTION_MAP.remove(dm.getHwnd());
    }

    public void stopAndUnbind() {
        stop();
        dm.unbind();
    }

    public static boolean stopAuction(int hwnd) {
        if (!isRunning(hwnd)) {
            return false;
        }

        Auction auction = GlobalVariable.AUCTION_MAP.get(hwnd);
        auction.stop();
        return true;
    }

    public static boolean startSellThread(int hwnd) {
        boolean running = Auction.isRunning(hwnd);
        if (running) {
            return false;
        }

        GlobalVariable.THREAD_POOL.execute(() -> Auction.createInstance(DmDdt.createInstance(hwnd)).goLoop());
        return true;
    }

    public void goLoop() {
        if (running) {
            log.info("[{}] 线程已经在运行中了", dm.getHwnd());
            return;
        }
        boolean isFlashWindow = dm.isWindowClassFlashPlayerActiveX();
        if (!isFlashWindow) {
            stop();
            return;
        }

        dm.bind();
        running = true;
        int n = 1;
        while (n <= AuctionConstants.AuctionPosition.N * AuctionConstants.AuctionPosition.N) {
            if (!dm.isBind()) {
                log.info("[{}] 绑定丢失，终止！", dm.getHwnd());
                break;
            }
            if (!running) {
                log.info("[{}] 运行结束！", dm.getHwnd());
                break;
            }
            if (stop || !dm.isWindowClassFlashPlayerActiveX()) {
                log.info("[{}] 运行终止！", dm.getHwnd());
                stop();
                break;
            }
            go(n);
            n++;
            Util.sleep(500L);
        }

        stop();
    }

    public void go(int n) {
        dm.leftClick(10, 10);
        Util.sleep(100L);
        // 点击背包第 x 个
        int[] point = AuctionConstants.AuctionPosition.getPoint(n);
        if (point == null) {
            return;
        }
        dm.leftClick(point);
        Util.sleep(600L);

        // 点击拍卖的地方
        dm.leftClick(AuctionConstants.AUCTION_INPUT_POINT);
        Util.sleep(300L);

        // ocr 物品名称
        String itemName = ocrItemName();
        // 检测是否弹框输入数量，并且获取数量
        Integer num = getNum();
        log.info("物品：{}, 数量：{}", itemName, num);

        boolean putBack = false;
        Integer[] price = null;
        AuctionList auctionList = SpringContextUtil.getBean(AuctionList.class);
        AuctionItem auctionItem = null;

        if (auctionList == null) {
            log.info("auctionList 为空，找不到");
            putBack = true;
        } else {
            auctionItem = auctionList.getItem(itemName);
        }
        if (auctionItem == null) {
            log.info("找不到");
            putBack = true;
        } else {
            if (!auctionItem.isEnabled()) {
                putBack = true;
            }
            price = auctionItem.getPrice(num);
            if (price == null) {
                log.info("价格无");
                putBack = true;
            }
        }

        if (putBack) {
            if (num != null && num > 1) {
                // 取消拍卖
                dm.leftClick(AuctionConstants.NUM_INPUT_BOX_CANCEL_POINT);
            } else {
                // 数量为 1 的放回原来位置
                dm.leftClick(AuctionConstants.AUCTION_INPUT_POINT);
                Util.sleep(750L);
                dm.leftClick(point);
            }
            return;
        } else if (num > 1) {
            // 确认数量
            dm.leftClick(AuctionConstants.NUM_INPUT_BOX_CONFIRM_POINT);
            Util.sleep(500L);
        }

        log.info("输入价格：竞拍价：{}*{}={}, 一口价：{}*{}={}, 时长：{}", auctionItem.getArgueUnitPrice(), num, price[0], auctionItem.getMouthfulUnitPrice(), num, price[1], auctionItem.getAuctionTime());

        // 双击竞拍价框
        dm.leftDoubleClick(AuctionConstants.ARGUE_PRICE_POINT);
        dm.leftDoubleClick(AuctionConstants.ARGUE_PRICE_POINT);
        Util.sleep(100L);
        // 删除数值
        dm.keyPressChar("back");
        if (price[0] != null) {
            Util.sleep(200L);
            // 如果有值，那么填入
            dm.pressKeyChars(AuctionItem.priceToStr(price[0]), 10L);
        }

        Util.sleep(200L);
        // 双击一口价
        dm.leftDoubleClick(AuctionConstants.MOUTHFUL_PRICE_POINT);
        dm.leftDoubleClick(AuctionConstants.MOUTHFUL_PRICE_POINT);
        Util.sleep(200L);
        // 删除数值
        dm.keyPressChar("back");
        if (price[1] != null) {
            // 如果有值，那么填入
            Util.sleep(100L);
            dm.pressKeyChars(AuctionItem.priceToStr(price[1]), 10L);
        }
        Util.sleep(200L);

        // 选择拍卖时间
        AuctionConstants.AuctionTimeEnum auctionTime = AuctionConstants.AuctionTimeEnum.getAuctionTime(auctionItem.getAuctionTime());
        dm.leftClick(auctionTime.getPosition());
        Util.sleep(100L);

        // 进行拍卖！
//        dm.leftClick(AuctionConstants.SELL_POINT);
    }

    public Integer getNum() {
        boolean inputNumBoxAppear = findInputNumBox("img/auction-num-template-1.bmp", 0.7);
        if (!inputNumBoxAppear) {
            return 1;
        }

        Integer num = ocrItemNum();
        return num == null ? 1 : num;
    }

    public boolean findInputNumBox(String templatePath, double threshold) {
        int[] rect = AuctionConstants.NUM_INPUT_BOX_FIND_SELL_RECT;
        int[] pic = dm.findPic(rect[0], rect[1], rect[2], rect[3], templatePath, "010101", threshold, 0);
        if (pic == null) {
            return false;
        }
        return pic[0] > 0;
    }

    public String ocrItemName() {
        String dir = "tmp/auction/";
        String filename = dir + dm.getHwnd() + "-item_name-" + System.currentTimeMillis() + ".png";
        dm.capturePicByRegion(filename, AuctionConstants.ITEM_INPUT_NAME_OCR_RECT);
        String s = OcrUtil.ocrAuctionItemName(filename);
        if (!new File(filename).delete()) {
            log.info("文件{}无法删除", filename);
        }
        return s;
    }

    public Integer ocrItemNum() {
        String dir = "tmp/auction/";
        String filename = dir + dm.getHwnd() + "-item_num" + System.currentTimeMillis() + ".png";
        dm.capturePicByRegion(filename, AuctionConstants.NUM_INPUT_OCR_RECT);
        Integer i = OcrUtil.ocrAuctionItemNum(filename);
        if (!new File(filename).delete()) {
            log.info("文件{}无法删除", filename);
        }
        return i;
    }

    public void closeNumInputBox() {
        dm.leftClick(AuctionConstants.NUM_INPUT_BOX_CANCEL_POINT);
    }

    public void captureSellNumSamplePic(String path) {
        dm.capturePicByRegion(path, AuctionConstants.NUM_INPUT_BOX_SELL_NUM_SAMPLE_RECT);
    }
}
