package cn.xiejx.ddtassistant.type.auction;

import cn.xiejx.ddtassistant.constant.GlobalVariable;
import cn.xiejx.ddtassistant.dm.DmDdt;
import cn.xiejx.ddtassistant.exception.MyInterruptException;
import cn.xiejx.ddtassistant.type.common.Common;
import cn.xiejx.ddtassistant.utils.OcrUtil;
import cn.xiejx.ddtassistant.utils.SpringContextUtil;
import cn.xiejx.ddtassistant.utils.Util;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

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

        // 打开拍卖场，转到拍卖栏
//        ensureInAuction();


        int n = 1;
        while (n <= AuctionConstants.AuctionPosition.N * AuctionConstants.AuctionPosition.N) {
            if (!dm.isBind()) {
                log.info("[{}] 绑定丢失，终止！", dm.getHwnd());
                break;
            }
            if (stop || !dm.isWindowClassFlashPlayerActiveX()) {
                log.info("[{}] 运行终止！", dm.getHwnd());
                stop();
                break;
            }
            try {
                go(n);
            } catch (MyInterruptException e) {
                log.info("[{}] 中止", dm.getHwnd());
                stop();
                break;
            }
            n++;
            Util.sleep(300L);
        }

        stop();
    }

    public void go(int n) {
        dm.leftClick(10, 10);
        Util.sleep(100L);

        testRunningWithException();

        // 获取第 n 个物品的坐标
        int[] point = AuctionConstants.AuctionPosition.getPoint(n);
        if (point == null) {
            return;
        }

        // 点击背包第 n 个
        dm.leftClick(point, 200L);
        Util.sleep(400L);

        // 点击拍卖的地方
        dm.leftClick(AuctionConstants.AUCTION_INPUT_POINT, 200L);
        Util.sleep(300L);

        testRunningWithException();

        // ocr 物品名称
        String itemName = ocrItemName();
        // 检测是否弹框输入数量，并且获取数量
        Integer num = getNum();
        log.info("物品：{}, 数量：{}", itemName, num);

        Integer[] price;
        AuctionData auctionData = SpringContextUtil.getBean(AuctionData.class);
        AuctionItem auctionItem;

        if (auctionData == null) {
            log.info("auctionData 为空！");
            putItemBack(num);
            return;
        }

        auctionItem = auctionData.getItem(itemName);
        if (auctionItem == null) {
            log.info("列表中找不到名为[{}]的配置，放回背包", itemName);
            putItemBack(num);
            return;
        }

        if (!auctionItem.isEnabled()) {
            log.info("物品[{}]未启用拍卖，放回背包", itemName);
            putItemBack(num);
            return;
        }

        // 物品启用之后，确认数量放入拍卖格子
        if (num > 1) {
            dm.leftClick(AuctionConstants.NUM_INPUT_BOX_CONFIRM_POINT, 100L);
            Util.sleep(200L);
        }

        if (!auctionItem.isDrop()) {
            // 不丢弃卖金币，那么获取价格
            price = auctionItem.getPrice(num);
            if (price == null) {
                log.info("列表中获取不到[{}]的价格，放回背包", itemName);
                putItemBack(num);
                return;
            }
        } else {
            // 丢弃卖金币
            log.info("丢弃物品[{}]卖金币", itemName);
            dropItem();
            return;
        }

        log.info("[{}]：竞拍价：{}*{}={}, 一口价：{}*{}={}, 时长：{}", auctionItem.getSuitableName(), auctionItem.getArgueUnitPrice(), num, price[0], auctionItem.getMouthfulUnitPrice(), num, price[1], auctionItem.getAuctionTime());

        // 双击竞拍价框
        dm.leftDoubleClick(AuctionConstants.ARGUE_PRICE_POINT);
        dm.leftDoubleClick(AuctionConstants.ARGUE_PRICE_POINT);
        Util.sleep(100L);
        // 删除数值
        dm.keyPressChar("back");
        dm.keyPressChar("back");
        if (price[0] != null) {
            Util.sleep(100L);
            // 如果有值，那么填入
            dm.pressKeyChars(AuctionItem.priceToStr(price[0]), 100L);
        }

        Util.sleep(100L);
        // 双击一口价
        dm.leftDoubleClick(AuctionConstants.MOUTHFUL_PRICE_POINT);
        dm.leftDoubleClick(AuctionConstants.MOUTHFUL_PRICE_POINT);
        Util.sleep(100L);
        // 删除数值
        dm.keyPressChar("back");
        dm.keyPressChar("back");
        if (price[1] != null) {
            // 如果有值，那么填入
            Util.sleep(100L);
            dm.pressKeyChars(AuctionItem.priceToStr(price[1]), 100L);
        }
        Util.sleep(200L);

        // 选择拍卖时间
        AuctionConstants.AuctionTimeEnum auctionTime = AuctionConstants.AuctionTimeEnum.getAuctionTime(auctionItem.getAuctionTime());
        dm.leftClick(auctionTime.getPosition());
        Util.sleep(100L);
        testRunningWithException();

        // 进行拍卖！
        dm.leftClick(AuctionConstants.SELL_POINT);
    }

    public void inputPrice() {

    }

    public boolean ensureInAuction() {
        if (!findAuctionTitlePic(null, 0.6)) {

        }

        return true;
    }

    public Integer getNum() {
        boolean inputNumBoxAppear = findInputNumBox("img/auction-num-template-1.bmp", 0.7);
        if (!inputNumBoxAppear) {
            return 1;
        }

        Integer num = ocrItemNum();
        return num == null ? 1 : num;
    }

    /**
     * 是否在拍卖场
     *
     * @param templatePath templatePath
     * @param threshold    threshold
     * @return boolean
     */
    public boolean findAuctionTitlePic(String templatePath, double threshold) {
        int[] pic = dm.findPic(AuctionConstants.AUCTION_TITLE_FIND_RECT, templatePath, "010101", threshold, 0);
        if (pic == null) {
            return false;
        }
        return pic[0] > 0;
    }

    public boolean gotoAuction() {
        if (!Common.findMorePic(dm, "", 0.6)) {
            log.info("[{}] 未找到 更多 按钮", dm.getHwnd());
            return false;
        }
        Common.clickMore(dm);
        return true;
    }

    public boolean findInputNumBox(String templatePath, double threshold) {
        int[] pic = dm.findPic(AuctionConstants.NUM_INPUT_BOX_FIND_SELL_RECT, templatePath, "010101", threshold, 0);
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

    public void dropItem() {

    }

    public void putItemBack(Integer num) {
        testRunningWithException();
        // 放回原来背包位置
        if (num != null && num > 1) {
            // 取消拍卖
            dm.leftClick(AuctionConstants.NUM_INPUT_BOX_CANCEL_POINT, 100L);
        } else {
            // 数量为 1 的放回原来位置
            dm.leftClick(AuctionConstants.AUCTION_INPUT_POINT);
            Util.sleep(300L);
            dm.leftClick(AuctionConstants.PUT_BACK_POINT, 100L);
        }
        Util.sleep(200L);
    }

    public void closeNumInputBox() {
        dm.leftClick(AuctionConstants.NUM_INPUT_BOX_CANCEL_POINT);
    }

    public void captureSellNumSamplePic(String path) {
        dm.capturePicByRegion(path, AuctionConstants.NUM_INPUT_BOX_SELL_NUM_SAMPLE_RECT);
    }

    public void testRunningWithException() {
        if (!this.running) {
            throw new MyInterruptException("中止运行");
        }
    }
}
