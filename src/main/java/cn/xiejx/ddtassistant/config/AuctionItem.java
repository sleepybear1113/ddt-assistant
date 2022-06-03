package cn.xiejx.ddtassistant.config;

import lombok.Data;

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

    private String name;
    private String ocrName;
    private Boolean enable;
    private Double argueUnitPrice;
    private Double mouthfulUnitPrice;
    private Integer minNum;
    private String auctionTime;

    public Integer[] getPrice(int num) {
        if (!Boolean.TRUE.equals(enable)) {
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
        return res;
    }

    public static String[] priceToStr(int price) {
        String s = String.valueOf(price);
        return s.split("");
    }

    public boolean getEnable() {
        return Boolean.TRUE.equals(enable);
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }
}
