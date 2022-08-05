package cn.xiejx.ddtassistant.type;

import cn.xiejx.ddtassistant.constant.Constants;
import cn.xiejx.ddtassistant.constant.GlobalVariable;
import cn.xiejx.ddtassistant.utils.ImgUtil;
import cn.xiejx.ddtassistant.utils.Util;
import org.apache.commons.collections4.CollectionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sleepybear
 */
public class TypeConstants {

    public static class TemplatePrefix {
        public static final String TEMP_STR = "非正式模板-";

        public static final String PVE_CAPTCHA_COUNT_DOWN = "副本-验证码-倒计时-";
        public static final String PVE_FLOP_BONUS = "副本-翻牌-游戏结算-";


        public static final String AUCTION_SOLD_OUT_INTRO = "拍卖场-拍卖说明-";
        public static final String AUCTION_NUM_BOX = "拍卖场-拍卖数量-";
        public static final String AUCTION_DROP_SOLD_OUT = "拍卖场-是否需要出售-";
        public static final String AUCTION_BAG_OPEN = "拍卖场-背包打开-";
        public static final String AUCTION_ADD_TO_AUCTION = "拍卖场-物品已经添加进拍卖行-";
        public static final String AUCTION_SEARCH_TAB_UNCHECK = "拍卖场-物品搜索-未选中-";
        public static final String AUCTION_SEARCH_TAB_CHECKED = "拍卖场-物品搜索-选中-";
        public static final String AUCTION_SOLD_OUT_TAB_UNCHECK = "拍卖场-拍卖物品-未选中-";
        public static final String AUCTION_SOLD_OUT_TAB_CHECKED = "拍卖场-拍卖物品-选中-";
        public static final String AUCTION_EQUIPMENT_TAB_UNCHECK = "拍卖场-装备栏-未选中-";
        public static final String AUCTION_EQUIPMENT_TAB_CHECKED = "拍卖场-装备栏-选中-";
        public static final String AUCTION_PROPS_TAB_UNCHECK = "拍卖场-道具栏-未选中-";
        public static final String AUCTION_PROPS_TAB_CHECKED = "拍卖场-道具栏-选中-";
        public static final String AUCTION_NUM_BOX_CONFIRM_CANCEL_BUTTON = "拍卖场-输入框-确认取消按钮-";
        public static final String AUCTION_DROP_CONFIRM_CANCEL_BUTTON = "拍卖场-卖金币-确认取消按钮-";


        public static final String COMMON_CONTINUE_GAME = "通用-点击屏幕继续游戏-";
        public static final String COMMON_SMALL_HORN = "通用-小喇叭-";
        public static final String COMMON_HOME_PAGE = "通用-游戏首页-";

        public static List<String> getAuctionPrefixList() {
            String s = "拍卖场";
            List<String> list = new ArrayList<>();
            List<String> allDeclaredFields = Util.getAllDeclaredFields(TemplatePrefix.class);
            for (String field : allDeclaredFields) {
                if (field.startsWith(s)) {
                    list.add(field);
                }
            }
            return list;
        }

        public static String getFullPathWithFullName(String path, String name, int index) {
            return (path == null ? "" : path.endsWith("/") ? path : path + "/") + (name == null ? null : name.endsWith("-") ? name : name + "-") + index + Constants.BMP_SUFFIX;
        }

        public static String getFullPathWithFullName(String name, int index) {
            return getFullPathWithFullName(Constants.TEMPLATE_PICTURE_DIR, name, index);
        }

        public static void initTemplateImgMap() {
            GlobalVariable.templateImgMap = TypeConstants.TemplatePrefix.getTemplateImgMap();
        }

        public static Map<String, List<String>> getTemplateImgMap() {
            return getTemplateImgMap(Constants.TEMPLATE_PICTURE_DIR);
        }

        public static Map<String, List<String>> getTemplateImgMap(String dir) {
            // 获取常量
            List<String> allDeclaredFields = Util.getAllDeclaredFields(TemplatePrefix.class);
            if (CollectionUtils.isEmpty(allDeclaredFields)) {
                return null;
            }
            allDeclaredFields.removeIf(TEMP_STR::equals);
            allDeclaredFields.sort(String::compareTo);
            Map<String, List<String>> templateMap = new HashMap<>();
            for (String field : allDeclaredFields) {
                templateMap.put(field, new ArrayList<>());
            }

            // 遍历文件夹的文件
            File imgDir = new File(dir);
            if (!imgDir.exists()) {
                return null;
            }
            File[] files = imgDir.listFiles();
            if (files == null || files.length == 0) {
                return null;
            }

            // 将 jpg png 转 bmp
            for (File file : files) {
                if (!file.isFile()) {
                    continue;
                }
                boolean flag = false;
                String fileName = file.getName();
                if (fileName.endsWith(Constants.JPG_SUFFIX)) {
                    flag = ImgUtil.imgConvert(dir + fileName, dir + fileName.replace(Constants.JPG_SUFFIX, Constants.BMP_SUFFIX));
                }
                if (fileName.endsWith(Constants.PNG_SUFFIX)) {
                    flag = ImgUtil.imgConvert(dir + fileName, dir + fileName.replace(Constants.PNG_SUFFIX, Constants.BMP_SUFFIX));
                }
                if (flag) {
                    boolean delete = file.delete();
                }
            }

            // 遍历所有 bmp 图片
            imgDir = new File(dir);
            files = imgDir.listFiles();
            if (files == null || files.length == 0) {
                return null;
            }
            // 所有模板图片
            List<String> bmpFileNameList = new ArrayList<>();
            for (File file : files) {
                if (!file.isFile()) {
                    continue;
                }
                String fileName = file.getName();
                if (fileName.endsWith(Constants.BMP_SUFFIX)) {
                    bmpFileNameList.add(dir + fileName);
                }
            }

            if (CollectionUtils.isEmpty(bmpFileNameList)) {
                return null;
            }
            bmpFileNameList.sort(String::compareTo);
            for (String bmpName : bmpFileNameList) {
                for (String field : allDeclaredFields) {
                    if (bmpName.contains(field)) {
                        templateMap.get(field).add(bmpName);
                        break;
                    }
                }
            }

            return templateMap;
        }
    }

}
