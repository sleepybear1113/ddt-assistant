package cn.xiejx.ddtassistant.type.auction;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

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
    private Double argueUnitPrice;
    /**
     * 一口价单价
     */
    private Double mouthfulUnitPrice;
    /**
     * 最小挂的数量
     */
    private Integer minNum;
    /**
     * 拍卖时限
     */
    private String auctionTime;
    /**
     * 丢掉卖金币
     */
    private Boolean drop;
    /**
     * 每组数量，需要大于等于 minNum
     */
    private Integer batchNum;

    public Integer[] getPrice(Integer num) {
        if (!Boolean.TRUE.equals(enabled) || num == null) {
            return null;
        }

        Integer[] res = {null, null};
        if (argueUnitPrice != null) {
            double arguePrice = argueUnitPrice * num;
            int i = BigDecimal.valueOf(arguePrice).setScale(0, RoundingMode.HALF_UP).intValue();
            res[0] = i;
        }
        if (mouthfulUnitPrice != null) {
            double mouthfulPrice = mouthfulUnitPrice * num;
            int i = BigDecimal.valueOf(mouthfulPrice).setScale(0, RoundingMode.HALF_UP).intValue();
            res[1] = i;
        }
        if (res[0] != null && res[1] != null) {
            if (res[1] <= res[0]) {
                res[1] = res[0] + 1;
            }
        }
        return res;
    }

    @JSONField(serialize = false)
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
}
