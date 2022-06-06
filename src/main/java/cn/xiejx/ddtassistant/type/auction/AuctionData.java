package cn.xiejx.ddtassistant.type.auction;

import cn.xiejx.ddtassistant.utils.Util;
import com.alibaba.fastjson2.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/06/02 21:22
 */
@Data
@Slf4j
public class AuctionData implements Serializable {

    private static final long serialVersionUID = -8918824441922553752L;

    private List<AuctionItem> auctionItemList;

    /**
     * OCR 不识别的加入列表
     */
    private Boolean autoAddUnknown;

    private AuctionData() {

    }

    public void update(AuctionData auctionData) {
        if (auctionData == null) {
            return;
        }
        auctionItemList = auctionData.getAuctionItemList();
        autoAddUnknown = auctionData.getAutoAddUnknown();
    }

    public AuctionItem getItem(String name) {
        if (CollectionUtils.isEmpty(auctionItemList)) {
            return null;
        }

        for (AuctionItem auctionItem : auctionItemList) {
            if (!auctionItem.getOcrName().equals(name) && !auctionItem.getName().equals(name)) {
                continue;
            }
            return auctionItem;
        }

        if (Boolean.TRUE.equals(autoAddUnknown)) {
            addNewUnknownItem(name);
            save();
        }
        return null;
    }

    public static AuctionData load() {
        String s = Util.readFile(AuctionConstants.FILENAME);
        AuctionData auctionData = new AuctionData();
        if (StringUtils.isBlank(s)) {
            return auctionData;
        }
        try {
            AuctionData data = JSON.parseObject(s, AuctionData.class);
            auctionData.setAuctionItemList(data.getAuctionItemList());
        } catch (Exception e) {
            log.warn("加载拍卖场自定义列表失败, {}", e.getMessage());
        }
        return auctionData;
    }

    public void save() {
        String s = JSON.toJSONString(this);
        Util.writeFile(s, AuctionConstants.FILENAME);
    }

    public void addNewUnknownItem(String name) {
        AuctionItem auctionItem = new AuctionItem();
        auctionItem.setOcrName(name);
        auctionItem.setEnabled(false);
        auctionItem.setAuctionTime("48");
        auctionItem.setMinNum(1);
        auctionItemList.add(auctionItem);
    }
}
