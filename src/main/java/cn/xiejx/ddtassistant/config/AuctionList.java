package cn.xiejx.ddtassistant.config;

import cn.xiejx.ddtassistant.constant.Constants;
import cn.xiejx.ddtassistant.utils.Util;
import com.alibaba.fastjson2.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
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
    private static final String FILENAME = Constants.CONFIG_DIR + "action-list-config.json";

    private static final long serialVersionUID = -8918824441922553752L;

    private List<AuctionItem> auctionItemList;

    private AuctionList() {

    }

    public AuctionItem getItem(String name) {
        if (CollectionUtils.isEmpty(auctionItemList)) {
            return null;
        }

        for (AuctionItem auctionItem : auctionItemList) {
            if (!auctionItem.getOcrName().equals(name)) {
                continue;
            }
            return auctionItem;
        }
        return null;
    }

    public static AuctionList load() {
        AuctionList auctionList = new AuctionList();
        String s = Util.readFile(FILENAME);
        auctionList.setAuctionItemList(new ArrayList<>());
        if (StringUtils.isBlank(s)) {
            return auctionList;
        }
        try {
            List<AuctionItem> list = JSON.parseArray(s, AuctionItem.class);
            auctionList.setAuctionItemList(list);
        } catch (Exception e) {
            log.warn("加载拍卖场自定义列表失败, {}", e.getMessage());
        }
        return auctionList;
    }

}
