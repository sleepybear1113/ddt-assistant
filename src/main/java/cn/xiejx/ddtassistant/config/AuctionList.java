package cn.xiejx.ddtassistant.config;

import cn.xiejx.ddtassistant.type.auction.AuctionConstants;
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
public class AuctionList implements Serializable {

    private static final long serialVersionUID = -8918824441922553752L;

    private List<AuctionItem> auctionItemList;

    private AuctionList() {

    }

    public void update(AuctionList auctionList) {
        if (auctionList == null) {
            return;
        }
        auctionItemList = auctionList.getAuctionItemList();
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
        return null;
    }

    public static AuctionList load() {
        String s = Util.readFile(AuctionConstants.FILENAME);
        AuctionList auctionList = new AuctionList();
        if (StringUtils.isBlank(s)) {
            return auctionList;
        }
        try {
            AuctionList data = JSON.parseObject(s, AuctionList.class);
            auctionList.setAuctionItemList(data.getAuctionItemList());
        } catch (Exception e) {
            log.warn("加载拍卖场自定义列表失败, {}", e.getMessage());
        }
        return auctionList;
    }

    public void save() {
        String s = JSON.toJSONString(this);
        Util.writeFile(s, AuctionConstants.FILENAME);
    }
}
