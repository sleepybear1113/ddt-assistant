package cn.xiejx.ddtassistant.type.auction;

import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * @author sleepybear
 */
@Data
public class AuctionPrice implements Serializable {
    private static final long serialVersionUID = -920027308169712438L;

    /**
     * 数量
     */
    private Integer num;
    /**
     * 竞拍价
     */
    private Integer arguePrice;
    /**
     * 一口价
     */
    private Integer mouthfulPrice;

    /**
     * 是否还有剩余的数量
     */
    private Boolean numberLeft;

    public static AuctionPrice parseAuctionPrice(List<String> minNum, List<Double> argueUnitPriceList, List<Double> mouthfulUnitPriceList, int num) {
        AuctionPrice auctionPrice = new AuctionPrice();

        // 符合数量要求的最小数量字符串
        String numberStr = null;
        // 遍历价格，是不是有符合的大于的数量
        int i = 0;
        for (; i < minNum.size(); i++) {
            String n = minNum.get(i);
            int intValue = new BigDecimal(n).intValue();
            if (num >= Math.abs(intValue)) {
                numberStr = n;
                break;
            }
        }
        if (numberStr == null) {
            return null;
        }

        // 去掉符号的最小数量
        int minNumber = Math.abs(new BigDecimal(numberStr).intValue());
        // 最终要输入的数量
        int number;

        // 首字符号
        char c = numberStr.charAt(0);
        if (c == '-') {
            // 如果是 - 号，那么直接赋值
            number = minNumber;
        } else if (c == '+') {
            // 如果是 + 号，那么获取最大整数倍数再赋值
            int n = num / minNumber;
            number = n * minNumber;
        } else {
            // 直接取最大数量
            number = num;
        }

        // 计算后的输入数量，和原有数量对比，是否还有剩余的数量
        auctionPrice.setNumberLeft(number < num);
        auctionPrice.setNum(number);

        Double argueUnitPrice = null;
        Double mouthfulUnitPrice = null;
        if (CollectionUtils.isNotEmpty(argueUnitPriceList)) {
            argueUnitPrice = argueUnitPriceList.size() < i ? argueUnitPriceList.get(argueUnitPriceList.size() - 1) : argueUnitPriceList.get(i);
        }
        if (CollectionUtils.isNotEmpty(mouthfulUnitPriceList)) {
            mouthfulUnitPrice = mouthfulUnitPriceList.size() < i ? mouthfulUnitPriceList.get(mouthfulUnitPriceList.size() - 1) : mouthfulUnitPriceList.get(i);
        }

        if (argueUnitPrice != null) {
            auctionPrice.setArguePrice(new BigDecimal(number * 1.0 * argueUnitPrice).setScale(0, RoundingMode.HALF_UP).intValue());
            if (auctionPrice.getArguePrice() <= 0) {
                auctionPrice.setArguePrice(1);
            }
        }
        if (mouthfulUnitPrice != null) {
            auctionPrice.setMouthfulPrice(new BigDecimal(number * 1.0 * mouthfulUnitPrice).setScale(0, RoundingMode.HALF_UP).intValue());
            // 如果竞拍价存在，那么一口价需要比竞拍价高
            Integer arguePrice0 = auctionPrice.getArguePrice();
            if (arguePrice0 != null) {
                if (arguePrice0 >= auctionPrice.getMouthfulPrice()) {
                    auctionPrice.setMouthfulPrice(arguePrice0 + 1);
                }
            }
        }

        return auctionPrice;
    }
}
