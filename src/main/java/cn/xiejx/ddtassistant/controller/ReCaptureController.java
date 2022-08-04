package cn.xiejx.ddtassistant.controller;

import cn.xiejx.ddtassistant.annotation.ReCaptureRequestMapping;
import cn.xiejx.ddtassistant.logic.ReCaptureLogic;
import cn.xiejx.ddtassistant.type.TypeConstants;
import cn.xiejx.ddtassistant.type.auction.Auction;
import cn.xiejx.ddtassistant.type.captcha.Captcha;
import cn.xiejx.ddtassistant.type.reCapture.RecaptureDomain;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

/**
 * @author sleepybear
 */
@RestController
public class ReCaptureController {
    public static final Random RANDOM = new Random();

    @Resource
    private ReCaptureLogic reCaptureLogic;

    @RequestMapping("/reCapture/deleteSampleImg")
    public Boolean deleteSampleImg(String src) {
        return reCaptureLogic.deleteSampleImg(src);
    }

    @RequestMapping("/reCapture/getTemplates")
    public List<String> getTemplates(String templatePrefix) {
        return reCaptureLogic.getTemplates(templatePrefix, true);
    }

    @RequestMapping("/reCapture/getAllTemplateInfoList")
    public List<RecaptureDomain> getAllTemplateInfoList() {
        return reCaptureLogic.getAllTemplateInfoList();
    }

    @RequestMapping("/reCapture/convertToOfficialTemplate")
    public Boolean convertToOfficialTemplate(String path) {
        return reCaptureLogic.convertToOfficialTemplate(path);
    }

    @ReCaptureRequestMapping(value = "/reCapture/captcha/pveFlopBonusSample", prefix = TypeConstants.TemplatePrefix.PVE_FLOP_BONUS, msg = "副本翻牌“游戏结算”")
    public List<String> pveFlopBonusSample(Integer[] hwnds) {
        return reCaptureLogic.invokeCapture(hwnds, "capturePveFlopBonusSampleRegion", getRandomTemplatePrefix(), Captcha.class);
    }

    @ReCaptureRequestMapping(value = "/reCapture/captcha/countdownSample", prefix = TypeConstants.TemplatePrefix.PVE_CAPTCHA_COUNT_DOWN, msg = "副本验证码“倒计时”")
    public List<String> countdownSample(Integer[] hwnds) {
        return reCaptureLogic.invokeCapture(hwnds, "captureCountDownSampleRegion", getRandomTemplatePrefix(), Captcha.class);
    }

    @ReCaptureRequestMapping(value = "/reCapture/auction/num", prefix = TypeConstants.TemplatePrefix.AUCTION_NUM_BOX, msg = "拍卖场“拍卖数量”")
    public List<String> auctionNum(Integer[] hwnds) {
        return reCaptureLogic.invokeCapture(hwnds, "captureSellNumSamplePic", getRandomTemplatePrefix(), Auction.class);
    }

    @ReCaptureRequestMapping(value = "/reCapture/auction/bagOpen", prefix = TypeConstants.TemplatePrefix.AUCTION_BAG_OPEN, msg = "拍卖场背包打开")
    public List<String> auctionBagOpen(Integer[] hwnds) {
        return reCaptureLogic.invokeCapture(hwnds, "captureBagOpenSamplePic", getRandomTemplatePrefix(), Auction.class);
    }

    @ReCaptureRequestMapping(value = "/reCapture/auction/sellIntro", prefix = TypeConstants.TemplatePrefix.AUCTION_SOLD_OUT_INTRO, msg = "拍卖场右上角“拍卖说明”")
    public List<String> auctionSellIntro(Integer[] hwnds) {
        return reCaptureLogic.invokeCapture(hwnds, "captureSellIntroSamplePic", getRandomTemplatePrefix(), Auction.class);
    }

    @ReCaptureRequestMapping(value = "/reCapture/auction/dropConfirmCancel", prefix = TypeConstants.TemplatePrefix.AUCTION_DROP_CONFIRM_CANCEL_BUTTON, msg = "拍卖场丢弃卖金币“确认-取消”按钮")
    public List<String> auctionDropConfirmCancel(Integer[] hwnds) {
        return reCaptureLogic.invokeCapture(hwnds, "captureDropConfirmCancelSamplePic", getRandomTemplatePrefix(), Auction.class);
    }

    @ReCaptureRequestMapping(value = "/reCapture/auction/numInputConfirmCancel", prefix = TypeConstants.TemplatePrefix.AUCTION_NUM_BOX_CONFIRM_CANCEL_BUTTON, msg = "拍卖场数量“确认-取消”按钮")
    public List<String> auctionNumInputConfirmCancel(Integer[] hwnds) {
        return reCaptureLogic.invokeCapture(hwnds, "captureNumInputConfirmCancelSamplePic", getRandomTemplatePrefix(), Auction.class);
    }

    @ReCaptureRequestMapping(value = "/reCapture/auction/soldOutTabChecked", prefix = TypeConstants.TemplatePrefix.AUCTION_SOLD_OUT_TAB_CHECKED, msg = "拍卖场上方选项卡“拍卖物品”选中状态")
    public List<String> auctionSoldOutTabChecked(Integer[] hwnds) {
        return reCaptureLogic.invokeCapture(hwnds, "captureSoldOutTabSamplePic", getRandomTemplatePrefix(), Auction.class);
    }

    @ReCaptureRequestMapping(value = "/reCapture/auction/soldOutTabUnChecked", prefix = TypeConstants.TemplatePrefix.AUCTION_SOLD_OUT_TAB_UNCHECK, msg = "拍卖场上方选项卡“拍卖物品”未选中状态")
    public List<String> auctionSoldOutTabUnChecked(Integer[] hwnds) {
        return reCaptureLogic.invokeCapture(hwnds, "captureSoldOutTabSamplePic", getRandomTemplatePrefix(), Auction.class);
    }

    @ReCaptureRequestMapping(value = "/reCapture/auction/equipmentTabChecked", prefix = TypeConstants.TemplatePrefix.AUCTION_EQUIPMENT_TAB_CHECKED, msg = "拍卖场背包选项卡“装备”选中状态")
    public List<String> auctionEquipmentTabChecked(Integer[] hwnds) {
        return reCaptureLogic.invokeCapture(hwnds, "captureEquipmentTabSamplePic", getRandomTemplatePrefix(), Auction.class);
    }

    @ReCaptureRequestMapping(value = "/reCapture/auction/equipmentTabUnChecked", prefix = TypeConstants.TemplatePrefix.AUCTION_EQUIPMENT_TAB_UNCHECK, msg = "拍卖场背包选项卡“装备”未选中状态")
    public List<String> auctionEquipmentTabUnChecked(Integer[] hwnds) {
        return reCaptureLogic.invokeCapture(hwnds, "captureEquipmentTabSamplePic", getRandomTemplatePrefix(), Auction.class);
    }

    @ReCaptureRequestMapping(value = "/reCapture/auction/propsTabChecked", prefix = TypeConstants.TemplatePrefix.AUCTION_PROPS_TAB_CHECKED, msg = "拍卖场背包选项卡“道具”选中状态")
    public List<String> auctionPropsTabChecked(Integer[] hwnds) {
        return reCaptureLogic.invokeCapture(hwnds, "capturePropsTabSamplePic", getRandomTemplatePrefix(), Auction.class);
    }

    @ReCaptureRequestMapping(value = "/reCapture/auction/prosTabUnChecked", prefix = TypeConstants.TemplatePrefix.AUCTION_PROPS_TAB_UNCHECK, msg = "拍卖场背包选项卡“道具”未选中状态")
    public List<String> auctionPropsTabUnChecked(Integer[] hwnds) {
        return reCaptureLogic.invokeCapture(hwnds, "capturePropsTabSamplePic", getRandomTemplatePrefix(), Auction.class);
    }

    @ReCaptureRequestMapping(value = "/reCapture/auction/searchTabChecked", prefix = TypeConstants.TemplatePrefix.AUCTION_SEARCH_TAB_CHECKED, msg = "拍卖场上方选项卡“搜索”选中状态")
    public List<String> auctionSearchTabChecked(Integer[] hwnds) {
        return reCaptureLogic.invokeCapture(hwnds, "captureSearchTabTabSamplePic", getRandomTemplatePrefix(), Auction.class);
    }

    @ReCaptureRequestMapping(value = "/reCapture/auction/searchTabUnChecked", prefix = TypeConstants.TemplatePrefix.AUCTION_SEARCH_TAB_UNCHECK, msg = "拍卖场上方选项卡“搜索”未选中状态")
    public List<String> auctionSearchTabUnChecked(Integer[] hwnds) {
        return reCaptureLogic.invokeCapture(hwnds, "captureSearchTabTabSamplePic", getRandomTemplatePrefix(), Auction.class);
    }

    public static String getRandomTemplatePrefix() {
        Class<?> clazz = ReCaptureController.class;
        Method[] declaredMethods = clazz.getDeclaredMethods();
        for (Method method : declaredMethods) {
            if (!method.getName().equals(Thread.currentThread().getStackTrace()[2].getMethodName())) {
                continue;
            }
            Annotation[] annotations = method.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation instanceof ReCaptureRequestMapping) {
                    String prefix = ((ReCaptureRequestMapping) annotation).prefix();
                    return TypeConstants.TemplatePrefix.getFullPathWithFullName(prefix + TypeConstants.TemplatePrefix.TEMP_STR, RANDOM.nextInt(900) + 100);
                }
            }
        }
        return null;
    }
}
