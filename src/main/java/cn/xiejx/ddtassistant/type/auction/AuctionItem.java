package cn.xiejx.ddtassistant.type.auction;

import cn.xiejx.ddtassistant.utils.Util;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/06/02 21:22
 */
@Data
public class AuctionItem implements Serializable {
    private static final long serialVersionUID = 3245791330843756688L;

    /**
     * 用户可见的自定义的名字
     */
    private String name;
    /**
     * ocr 名字
     */
    private String ocrName;
    /**
     * 是否启用
     */
    private Boolean enabled;
    /**
     * 竞拍价单价
     */
    private String argueUnitPrice;
    /**
     * 一口价单价
     */
    private String mouthfulUnitPrice;
    /**
     * 最小挂的数量
     */
    private String minNum;
    /**
     * 拍卖时限
     */
    private String auctionTime;
    /**
     * 丢掉卖金币
     */
    private Boolean drop;

    @JsonIgnore
    public List<String> getMinNumList() {
        if (StringUtils.isBlank(this.minNum)) {
            return new ArrayList<>();
        }
        String[] split = this.minNum.split(",");
        return Arrays.asList(split);
    }

    @JsonIgnore
    public List<Double> getArgueUnitPriceList() {
        ArrayList<Double> list = new ArrayList<>();
        if (StringUtils.isBlank(this.argueUnitPrice)) {
            return list;
        }
        String[] split = this.argueUnitPrice.split(",");
        for (String s : split) {
            list.add(Double.valueOf(s));
        }
        return list;
    }

    @JsonIgnore
    public List<Double> getMouthfulUnitPriceList() {
        ArrayList<Double> list = new ArrayList<>();
        if (StringUtils.isBlank(this.mouthfulUnitPrice)) {
            return list;
        }
        String[] split = this.mouthfulUnitPrice.split(",");
        for (String s : split) {
            list.add(Double.valueOf(s));
        }
        return list;
    }

    @JsonIgnore
    public AuctionPrice getPrice(Integer num) {
        if (!Boolean.TRUE.equals(this.enabled) || num == null) {
            return null;
        }
        List<String> minNumList = getMinNumList();
        if (CollectionUtils.isEmpty(minNumList)) {
            return null;
        }
        return AuctionPrice.parseAuctionPrice(minNumList, getArgueUnitPriceList(), getMouthfulUnitPriceList(), num);
    }

    @JsonIgnore
    public String getSuitableName() {
        return StringUtils.isNotBlank(this.name) ? this.name : this.ocrName;
    }

    public static String[] priceToStr(int price) {
        String s = String.valueOf(price);
        return s.split("");
    }

    public boolean isEnabled() {
        return Boolean.TRUE.equals(enabled);
    }

    public boolean isDrop() {
        return Boolean.TRUE.equals(this.drop);
    }

    public void setMinNum(String minNum) {
        ArrayList<String> list = new ArrayList<>();
        if (StringUtils.isNotBlank(minNum)) {
            String[] nums = minNum.replace("，", ",").split(",");
            for (String num : nums) {
                String number = Util.getIntegerNumberWithSign(num);
                if (number != null) {
                    list.add(number);
                }
            }
        }
        this.minNum = StringUtils.join(list, ",");
    }

    public void setMinNum(Integer minNum) {
        setMinNum(String.valueOf(minNum));
    }

    public void setArgueUnitPrice(String argueUnitPrice) {
        this.argueUnitPrice = parsePrice(argueUnitPrice);
    }

    public void setArgueUnitPrice(Double argueUnitPrice) {
        setArgueUnitPrice(String.valueOf(argueUnitPrice));
    }

    public void setMouthfulUnitPrice(String mouthfulUnitPrice) {
        this.mouthfulUnitPrice = parsePrice(mouthfulUnitPrice);
    }

    public void setMouthfulUnitPrice(Double mouthfulUnitPrice) {
        setMouthfulUnitPrice(String.valueOf(mouthfulUnitPrice));
    }

    private static String parsePrice(String priceStr) {
        ArrayList<Double> list = new ArrayList<>();
        if (StringUtils.isNotBlank(priceStr)) {
            String[] prices = priceStr.replace("，", ",").split(",");
            for (String price : prices) {
                if (Util.isNumber(price)) {
                    list.add(Double.valueOf(price));
                }
            }
        }
        return StringUtils.join(list, ",");
    }
}
