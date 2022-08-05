package cn.xiejx.ddtassistant.type.auction;

import cn.xiejx.ddtassistant.constant.Constants;
import cn.xiejx.ddtassistant.constant.GlobalVariable;
import cn.xiejx.ddtassistant.dm.DmConstants;
import cn.xiejx.ddtassistant.dm.DmDdt;
import cn.xiejx.ddtassistant.dm.DmDomains;
import cn.xiejx.ddtassistant.exception.MyInterruptException;
import cn.xiejx.ddtassistant.type.BaseType;
import cn.xiejx.ddtassistant.type.TypeConstants;
import cn.xiejx.ddtassistant.type.common.Common;
import cn.xiejx.ddtassistant.utils.ImgUtil;
import cn.xiejx.ddtassistant.utils.OcrUtil;
import cn.xiejx.ddtassistant.utils.SpringContextUtil;
import cn.xiejx.ddtassistant.utils.Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/06/02 20:42
 */
@Slf4j
public class Auction extends BaseType {

    private static final long serialVersionUID = 8932679197291875438L;
    private boolean stop;

    private boolean confirm;

    public Auction() {
    }

    private Auction(DmDdt dm) {
        init(dm);
    }

    @Override
    public void init(DmDdt dm) {
        super.init(dm);
        this.stop = false;
    }

    public static boolean stopAuction(int hwnd) {
        if (!isRunning(hwnd, Auction.class)) {
            return false;
        }
        removeByType(hwnd, Auction.class);
        return true;
    }

    public static boolean startSellThread(int hwnd, boolean confirm) {
        if (isRunning(hwnd, Auction.class)) {
            return false;
        }
        GlobalVariable.THREAD_POOL.execute(() -> Auction.createInstance(hwnd, Auction.class, false).goLoop(confirm));
        return true;
    }

    public void goLoop(boolean confirm) {
        log.info("[{}] 准备运行运行拍卖场功能", getHwnd());
        this.confirm = confirm;
        if (isRunning()) {
            log.info("[{}] 线程已经在运行中了", getHwnd());
            return;
        }
        boolean isFlashWindow = getDm().isWindowClassFlashPlayerActiveX();
        if (!isFlashWindow) {
            unbindAndRemove();
            return;
        }

        log.info("[{}] 开始拍卖场功能进行拍卖", getHwnd());
        getDm().bind();
        setRunning(true);

        getDm().clickCorner();
        Util.sleep(100L);

        // 打开拍卖场，转到拍卖栏
        if (!ensureInAuction()) {
            remove();
            return;
        }
        putBackItem();

        AuctionData auctionData = SpringContextUtil.getBean(AuctionData.class);
//        auctionData = AuctionData.load();
        if (auctionData == null) {
            remove();
            return;
        }

        AuctionConstants.AuctionPosition.SellType sellType = AuctionConstants.AuctionPosition.SellType.getSellType(auctionData.getSellType());
        // 选择装备栏或者道具栏的点击位置
        int[][] clickPoints = {null, null};
        if (sellType == AuctionConstants.AuctionPosition.SellType.PROPS_AND_EQUIPMENT) {
            clickPoints[0] = AuctionConstants.PROPS_TAB_POINT;
            clickPoints[1] = AuctionConstants.EQUIPMENT_TAB_POINT;
        } else if (sellType == AuctionConstants.AuctionPosition.SellType.EQUIPMENT_AND_PROPS) {
            clickPoints[0] = AuctionConstants.EQUIPMENT_TAB_POINT;
            clickPoints[1] = AuctionConstants.PROPS_TAB_POINT;
        } else if (sellType == AuctionConstants.AuctionPosition.SellType.EQUIPMENT_ONLY) {
            clickPoints[0] = AuctionConstants.EQUIPMENT_TAB_POINT;
        } else if (sellType == AuctionConstants.AuctionPosition.SellType.PROPS_ONLY) {
            clickPoints[0] = AuctionConstants.PROPS_TAB_POINT;
        }

        for (int[] clickPoint : clickPoints) {
            if (clickPoint == null) {
                continue;
            }

            // 点击道具栏活装备栏
            getDm().leftClick(clickPoint);
            Util.sleep(200L);
            getDm().leftClick(clickPoint);
            Util.sleep(200L);

            // 背包截图，来确定物品位置
            String bagPicPath = Constants.AUCTION_TMP_DIR + "bag-" + getHwnd() + "-" + System.currentTimeMillis() + ".png";
            captureBagArea(bagPicPath);
            List<Integer> indexes = AuctionConstants.AuctionPosition.getNotEmptyBagIndex(bagPicPath);
            Util.delayDeleteFile(bagPicPath, 100L);

            if (CollectionUtils.isEmpty(indexes)) {
                log.info("[{}] 背包为空，拍卖场功能结束！", getHwnd());
                remove();
                return;
            }
            log.info("[{}] 背包需要拍卖位置：{}", getHwnd(), indexes);

            // 开始逐个拍卖
            for (int i = 0; i < indexes.size(); i++) {
                Integer index = indexes.get(i);
                if (!getDm().isBind()) {
                    log.info("[{}] 绑定丢失，终止！", getHwnd());
                    break;
                }
                if (stop || !getDm().isWindowClassFlashPlayerActiveX()) {
                    log.info("[{}] 运行终止！", getHwnd());
                    break;
                }
                try {
                    // 开始拍卖
                    log.info("[{}] 拍卖第 {} 个！", getHwnd(), index);
                    Boolean left = go(index);
                    if (Boolean.TRUE.equals(left)) {
                        i--;
                    }
                } catch (MyInterruptException e) {
                    log.info("[{}] 中止", getHwnd());
                    break;
                }
                Util.sleep(300L);
            }
        }

        log.info("[{}] 拍卖完毕！", getHwnd());
        remove();
    }

    public Boolean go(int n) {
        getDm().clickCorner();
        Util.sleep(100L);

        testRunningWithException();

        // 获取第 n 个物品的坐标
        int[] point = AuctionConstants.AuctionPosition.getPoint(n);
        if (point == null) {
            return null;
        }

        // 点击背包第 n 个
        getDm().leftClick(point, 200L);
        Util.sleep(400L);

        // 点击拍卖的地方
        getDm().leftClick(AuctionConstants.AUCTION_INPUT_POINT, 200L);
        Util.sleep(300L);
        testRunningWithException();

        // ocr 物品名称
        String itemName = ocrItemName();
        // 检测是否弹框输入数量，并且获取数量
        Integer num = getNum();
        int times = 3;
        for (int i = 0; i < times; i++) {
            if (num > 0) {
                break;
            }
            log.info("物品：{}, 数量识别失败，重新识别", itemName);
            Util.sleep(400L);
            num = getNum();
        }
        if (num < 0) {
            log.info("物品：{}, 重试 {} 次，数量识别失败，放回背包", itemName, times);
            putItemBack(num);
            return null;
        }
        log.info("物品：{}, 数量：{}", itemName, num);

        AuctionData auctionData = SpringContextUtil.getBean(AuctionData.class);
//        auctionData = AuctionData.load();

        if (auctionData == null) {
            log.info("auctionData 为空！");
            putItemBack(num);
            return null;
        }

        // 获取物品名
        AuctionItem auctionItem = auctionData.getItem(itemName);
        if (auctionItem == null) {
            log.info("列表中找不到名为[{}]的配置，重新识别", itemName);
            itemName = ocrItemName();
            auctionItem = auctionData.getItem(itemName);
        }
        if (auctionItem == null) {
            log.info("列表中找不到名为[{}]的配置，放回背包", itemName);
            putItemBack(num);
            return null;
        }

        // 未启用拍卖
        if (!auctionItem.isEnabled()) {
            log.info("物品[{}]未启用拍卖，放回背包", itemName);
            putItemBack(num);
            return null;
        }

        // 物品要被丢弃的
        if (Boolean.TRUE.equals(auctionItem.getDrop())) {
            // 物品数量大于 1 那么需要确认数量放入拍卖格子
            if (num > 1) {
                getDm().leftClick(AuctionConstants.NUM_INPUT_BOX_CONFIRM_POINT, 100L);
                Util.sleep(200L);
            }
            // 丢弃卖金币
            log.info("丢弃物品[{}]卖金币", itemName);
            dropItem();
            return null;
        }

        // 价格数量配置
        AuctionPrice auctionPrice = auctionItem.getPrice(num);
        if (auctionPrice == null) {
            log.info("列表中获取不到[{}]的价格，放回背包", itemName);
            putItemBack(num);
            return null;
        }

        // 只输入部分数量
        if (auctionPrice.getNumberLeft()) {
            getDm().leftDoubleClick(AuctionConstants.NUM_INPUT_BOX_NUM_POINT);
            getDm().leftDoubleClick(AuctionConstants.NUM_INPUT_BOX_NUM_POINT);
            Integer inputNum = auctionPrice.getNum();
            getDm().pressKeyChars(AuctionItem.priceToStr(inputNum));
        }

        // 物品数量大于 1 那么需要确认数量放入拍卖格子
        if (num > 1) {
            getDm().leftClick(AuctionConstants.NUM_INPUT_BOX_CONFIRM_POINT, 100L);
            Util.sleep(200L);
        }

        log.info("[{}]：{}*{}={}, {}*{}={}, {}", auctionItem.getSuitableName(), auctionItem.getArgueUnitPrice(), auctionPrice.getNum(), auctionPrice.getArguePrice(), auctionItem.getMouthfulUnitPrice(), auctionPrice.getNum(), auctionPrice.getMouthfulPrice(), auctionItem.getAuctionTime());

        ensureArguePrice(auctionPrice);
        Util.sleep(100L);
        ensureMouthfulPrice(auctionPrice);
        Util.sleep(100L);

        // 选择拍卖时间
        AuctionConstants.AuctionTimeEnum auctionTime = AuctionConstants.AuctionTimeEnum.getAuctionTime(auctionItem.getAuctionTime());
        getDm().leftClick(auctionTime.getPosition());
        Util.sleep(100L);
        testRunningWithException();

        // 进行拍卖！
        if (Boolean.TRUE.equals(this.confirm)) {
            getDm().leftClick(AuctionConstants.SELL_POINT);
        } else {
            putItemBack(1);
        }
        return auctionPrice.getNumberLeft();
    }

    public boolean ensureInAuction() {
        List<String> templates = TypeConstants.TemplatePrefix.getAuctionPrefixList();
        List<String> templateImgList = GlobalVariable.getTemplateImgList(templates);
        List<DmDomains.PicEx> picExList = getDm().findPicExInFullGame(templateImgList, "020202", 0.7);

        if (DmDomains.PicEx.contains(picExList, templates)) {
            log.info("[{}] 在拍卖场", getHwnd());
        } else {
            log.info("[{}] 不在拍卖场，脚本停止！", getHwnd());
            return false;
        }

        picExList = closeMsgBox1(picExList, templateImgList);
        picExList = closeMsgBox2(picExList, templateImgList);
        picExList = ensureActiveSoldOutTab(picExList, templateImgList);
        picExList = ensureActiveBag(picExList, templateImgList);
        picExList = closeMsgBox1(picExList, templateImgList);
        picExList = closeMsgBox2(picExList, templateImgList);

        return !CollectionUtils.isEmpty(picExList);
    }

    public List<DmDomains.PicEx> ensureActiveSoldOutTab(List<DmDomains.PicEx> picExList, List<String> templateImgList) {
        if (CollectionUtils.isEmpty(picExList)) {
            return null;
        }

        List<String> checkList = new ArrayList<>();
        checkList.add(TypeConstants.TemplatePrefix.AUCTION_NUM_BOX);
        checkList.add(TypeConstants.TemplatePrefix.AUCTION_DROP_SOLD_OUT);
        checkList.add(TypeConstants.TemplatePrefix.AUCTION_SOLD_OUT_TAB_CHECKED);
        for (int i = 0; i < 3; i++) {
            if (DmDomains.PicEx.contains(picExList, checkList)) {
                log.info("[{}] 拍卖物品选项卡已选中", getHwnd());
                return picExList;
            }

            log.info("[{}] 点击拍卖物品选项卡", getHwnd());
            getDm().leftClick(AuctionConstants.SOLD_OUT_TAB_POINT);
            Util.sleep(2000L);
            picExList = getDm().findPicExInFullGame(templateImgList, "020202", 0.7);
        }
        log.info("[{}] 无法打开拍卖物品选项卡", getHwnd());
        return null;
    }

    public List<DmDomains.PicEx> ensureActiveBag(List<DmDomains.PicEx> picExList, List<String> templateImgList) {
        if (CollectionUtils.isEmpty(picExList)) {
            return null;
        }

        List<String> checkList = new ArrayList<>();
        checkList.add(TypeConstants.TemplatePrefix.AUCTION_NUM_BOX);
        checkList.add(TypeConstants.TemplatePrefix.AUCTION_DROP_SOLD_OUT);
        checkList.add(TypeConstants.TemplatePrefix.AUCTION_BAG_OPEN);
        for (int i = 0; i < 3; i++) {
            if (DmDomains.PicEx.contains(picExList, checkList)) {
                log.info("[{}] 背包打开了", getHwnd());
                return picExList;
            }

            log.info("[{}] 点击打开背包", getHwnd());
            getDm().leftClick(AuctionConstants.AUCTION_INPUT_POINT);
            Util.sleep(500L);
            picExList = getDm().findPicExInFullGame(templateImgList, "020202", 0.7);
        }
        log.info("[{}] 无法打开背包", getHwnd());
        return null;
    }

    /**
     * 关闭卖金币弹窗
     *
     * @param picExList       picExList
     * @param templateImgList templateImgList
     * @return List
     */
    public List<DmDomains.PicEx> closeMsgBox1(List<DmDomains.PicEx> picExList, List<String> templateImgList) {
        if (CollectionUtils.isEmpty(picExList)) {
            return null;
        }

        for (int i = 0; i < 3; i++) {
            if (!DmDomains.PicEx.contains(picExList, TypeConstants.TemplatePrefix.AUCTION_DROP_CONFIRM_CANCEL_BUTTON)) {
                return picExList;
            }

            log.info("[{}] 关闭卖金币弹窗", getHwnd());
            getDm().leftClick(AuctionConstants.SOLD_OUT_BOX_CANCEL_POINT);
            Util.sleep(500L);
            picExList = getDm().findPicExInFullGame(templateImgList, "020202", 0.7);
        }
        log.info("[{}] 无法关闭卖金币弹窗", getHwnd());
        return null;
    }

    /**
     * 关闭拍卖数量弹窗
     *
     * @param picExList       picExList
     * @param templateImgList templateImgList
     * @return List
     */
    public List<DmDomains.PicEx> closeMsgBox2(List<DmDomains.PicEx> picExList, List<String> templateImgList) {
        if (CollectionUtils.isEmpty(picExList)) {
            return null;
        }

        for (int i = 0; i < 3; i++) {
            if (!DmDomains.PicEx.contains(picExList, TypeConstants.TemplatePrefix.AUCTION_NUM_BOX)) {
                return picExList;
            }

            log.info("[{}] 关闭拍卖数量弹窗", getHwnd());
            getDm().leftClick(AuctionConstants.NUM_INPUT_BOX_CANCEL_POINT);
            Util.sleep(500L);
            picExList = getDm().findPicExInFullGame(templateImgList, "020202", 0.7);
        }
        log.info("[{}] 无法关闭拍卖数量弹窗", getHwnd());
        return null;
    }

    public void putBackItem() {
        String path = Constants.AUCTION_TMP_DIR + "item-icon-" + getHwnd() + "-" + System.currentTimeMillis() + ".png";
        getDm().capturePicByRegion(path, AuctionConstants.ITEM_INPUT_ICON_RECT);
        int[] avgColor = ImgUtil.getAvgColor(path);
        int[] delta = {30, 30, 30};
        Util.delayDeleteFile(path, null);
        if (ImgUtil.colorInDelta(avgColor, AuctionConstants.ITEM_INPUT_ICON_STANDARD_COLOR, delta)) {
            return;
        }
        // 有色差，表示有物品，需放回
        log.info("[{}] 将已放入物品放回", getHwnd());
        Util.sleep(300L);
        putItemBack(1);
    }

    public Integer getNum() {
        ArrayList<String> templates = new ArrayList<>();
        templates.add(TypeConstants.TemplatePrefix.AUCTION_NUM_BOX_CONFIRM_CANCEL_BUTTON);
        List<String> templateImgList = GlobalVariable.getTemplateImgList(templates);
        List<DmDomains.PicEx> picEx = getDm().findPicEx(AuctionConstants.NUM_INPUT_OR_DROP_CONFIRM_CANCEL_FIND_RECT, templateImgList, "030303", 0.6);
        if (CollectionUtils.isEmpty(picEx)) {
            return 1;
        }

        Integer num = ocrItemNum();
        return (num == null || num == 1) ? -1 : num;
    }

    public void waitAddToAuctionMsgDisappear() {
        List<String> templateImgList = GlobalVariable.getTemplateImgList(TypeConstants.TemplatePrefix.AUCTION_ADD_TO_AUCTION);
        int notFoundCount = 0;
        boolean first = true;
        while (notFoundCount < 1) {
            if (findAddToAuctionMsg(StringUtils.join(templateImgList, "|"), 0.7)) {
                Util.sleep(200L);
                first = false;
            }
            if (first) {
                break;
            }
            notFoundCount++;
            log.info("wait!!!");
        }
    }

    /**
     * 是否在拍卖场
     *
     * @param templatePath templatePath
     * @param threshold    threshold
     * @return boolean
     */
    public boolean findAuctionTitlePic(String templatePath, double threshold) {
        int[] pic = getDm().findPic(AuctionConstants.AUCTION_TITLE_FIND_RECT, templatePath, "010101", threshold, DmConstants.SearchWay.LEFT2RIGHT_UP2DOWN);
        if (pic == null) {
            return false;
        }
        return pic[0] > 0;
    }

    public boolean findAddToAuctionMsg(String templatePath, double threshold) {
        int[] pic = getDm().findPic(AuctionConstants.ADD_TO_AUCTION_MEG_FIND_RECT, templatePath, "010101", threshold, DmConstants.SearchWay.LEFT2RIGHT_UP2DOWN);
        if (pic == null) {
            return false;
        }
        return pic[0] > 0;
    }

    public boolean gotoAuction() {
        if (!Common.findMorePic(getDm(), "", 0.6)) {
            log.info("[{}] 未找到 更多 按钮", getHwnd());
            return false;
        }
        Common.clickMore(getDm());
        return true;
    }

    public boolean findInputNumBox(String templatePath, double threshold) {
        int[] pic = getDm().findPic(AuctionConstants.NUM_INPUT_BOX_FIND_SELL_RECT, templatePath, "010101", threshold, DmConstants.SearchWay.LEFT2RIGHT_UP2DOWN);
        if (pic == null) {
            return false;
        }
        return pic[0] > 0;
    }

    public String ocrItemName() {
        String filename = Constants.AUCTION_TMP_DIR + getHwnd() + "-item_name-" + System.currentTimeMillis() + ".png";
        getDm().capturePicByRegion(filename, AuctionConstants.ITEM_INPUT_NAME_OCR_RECT);
        String s = OcrUtil.ocrAuctionItemName(filename);
        if (!new File(filename).delete()) {
            log.info("文件{}无法删除", filename);
        }
        return s;
    }

    public Integer ocrItemArguePrice() {
        String filename = Constants.AUCTION_TMP_DIR + getHwnd() + "-item_argue_price-" + System.currentTimeMillis() + ".png";
        getDm().capturePicByRegion(filename, AuctionConstants.ARGUE_PRICE_OCR_RECT);
        Integer price = OcrUtil.ocrAuctionItemArguePrice(filename);
        if (!new File(filename).delete()) {
            log.info("文件{}无法删除", filename);
        }
        return price;
    }

    public Integer ocrItemMouthfulPrice() {
        String filename = Constants.AUCTION_TMP_DIR + getHwnd() + "-item_mouthful_price-" + System.currentTimeMillis() + ".png";
        getDm().capturePicByRegion(filename, AuctionConstants.MOUTHFUL_PRICE_OCR_RECT);
        Integer price = OcrUtil.ocrAuctionItemArguePrice(filename);
        if (!new File(filename).delete()) {
            log.info("文件{}无法删除", filename);
        }
        return price;
    }

    public Integer ocrItemNum() {
        String filename = Constants.AUCTION_TMP_DIR + getHwnd() + "-item_num" + System.currentTimeMillis() + ".png";
        captureItemNum(filename);
        Integer i = OcrUtil.ocrAuctionItemNum(filename);
        if (!new File(filename).delete()) {
            log.info("文件{}无法删除", filename);
        }
        return i;
    }

    public void captureItemNum(String path) {
        getDm().capturePicByRegion(path, AuctionConstants.NUM_INPUT_OCR_RECT);
    }

    public void dropItem() {
        // 点击拍卖的地方
        getDm().leftClick(AuctionConstants.AUCTION_INPUT_POINT, 200L);
        Util.sleep(300L);
        // 点击拍卖场下方出售丢弃
        getDm().leftClick(AuctionConstants.BOTTOM_BLANK_AREA_POINT, 200L);
        Util.sleep(100L);
        // 点击卖金币
        if (Boolean.TRUE.equals(this.confirm)) {
            getDm().leftClick(AuctionConstants.SOLD_OUT_BOX_CONFIRM_POINT, 200L);
        } else {
            getDm().leftClick(AuctionConstants.SOLD_OUT_BOX_CANCEL_POINT, 200L);
        }
    }

    public void ensureArguePrice(AuctionPrice auctionPrice) {
        Integer price = auctionPrice.getArguePrice();
        Integer existPrice = ocrItemArguePrice();
        if (Objects.equals(existPrice, price)) {
            return;
        }

        ensurePrice(price, AuctionConstants.ARGUE_PRICE_POINT);
    }

    public void ensureMouthfulPrice(AuctionPrice auctionPrice) {
        Integer price = auctionPrice.getMouthfulPrice();
        Integer existPrice = ocrItemMouthfulPrice();
        if (Objects.equals(existPrice, price)) {
            return;
        }

        Util.sleep(100L);
        ensurePrice(price, AuctionConstants.MOUTHFUL_PRICE_POINT);
    }

    public void ensurePrice(Integer price, int[] xy) {
        // 双击输入框
        getDm().leftDoubleClick(xy);
        getDm().leftDoubleClick(xy);
        Util.sleep(100L);
        // 删除数值
        getDm().keyPressChar("back");
        getDm().keyPressChar("back");

        if (price == null) {
            return;
        }

        Util.sleep(100L);
        getDm().pressKeyChars(AuctionItem.priceToStr(price), 100L);
    }

    public void putItemBack(Integer num) {
        testRunningWithException();
        // 放回原来背包位置
        if (num != null && (num > 1 || num < 0)) {
            // 取消拍卖
            getDm().leftClick(AuctionConstants.NUM_INPUT_BOX_CANCEL_POINT, 100L);
        } else {
            // 数量为 1 的放回原来位置
            getDm().leftClick(AuctionConstants.PUT_BACK_POINT, 100L);
            getDm().leftClick(AuctionConstants.AUCTION_INPUT_POINT);
            Util.sleep(300L);
            getDm().leftClick(AuctionConstants.PUT_BACK_POINT, 100L);
        }
        Util.sleep(200L);
    }

    public void closeNumInputBox() {
        getDm().leftClick(AuctionConstants.NUM_INPUT_BOX_CANCEL_POINT);
    }

    public void captureItemNameOcrRect(String path) {
        getDm().capturePicByRegion(path, AuctionConstants.ITEM_INPUT_NAME_OCR_RECT);
    }

    public void captureSellNumSamplePic(String path) {
        getDm().capturePicByRegion(path, AuctionConstants.NUM_INPUT_BOX_SELL_NUM_SAMPLE_RECT);
    }

    public void captureBagOpenSamplePic(String path) {
        getDm().capturePicByRegion(path, AuctionConstants.BAG_OPEN_TEMPLATE_RECT);
    }

    public void captureSellIntroSamplePic(String path) {
        getDm().capturePicByRegion(path, AuctionConstants.SOLD_OUT_INTRO_TEMPLATE_RECT);
    }

    public void captureDropConfirmCancelSamplePic(String path) {
        getDm().capturePicByRegion(path, AuctionConstants.NUM_INPUT_OR_DROP_CONFIRM_CANCEL_SAMPLE_RECT);
    }

    public void captureNumInputConfirmCancelSamplePic(String path) {
        getDm().capturePicByRegion(path, AuctionConstants.NUM_INPUT_OR_DROP_CONFIRM_CANCEL_SAMPLE_RECT);
    }

    public void captureSoldOutTabSamplePic(String path) {
        getDm().capturePicByRegion(path, AuctionConstants.SOLD_OUT_TAB_TEMPLATE_RECT);
    }

    public void captureSearchTabTabSamplePic(String path) {
        getDm().capturePicByRegion(path, AuctionConstants.SEARCH_TAB_TEMPLATE_RECT);
    }

    public void captureEquipmentTabSamplePic(String path) {
        getDm().capturePicByRegion(path, AuctionConstants.EQUIPMENT_TAB_TEMPLATE_RECT);
    }

    public void capturePropsTabSamplePic(String path) {
        getDm().capturePicByRegion(path, AuctionConstants.PROPS_TAB_TEMPLATE_RECT);
    }

    public void captureBagArea(String path) {
        getDm().capturePicByRegion(path, AuctionConstants.BAG_AREA_RECT);
    }

    public void captureSoldOutMsg(String path) {
        getDm().capturePicByRegion(path, AuctionConstants.SOLD_OUT_BOX_CONFIRM_SAMPLE_RECT);
    }

    public void captureAuctionTitle(String path) {
        getDm().capturePicByRegion(path, AuctionConstants.AUCTION_TITLE_SAMPLE_RECT);
    }

    public void testRunningWithException() {
        if (!isRunning()) {
            throw new MyInterruptException("中止运行");
        }
    }
}
