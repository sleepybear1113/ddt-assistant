package cn.xiejx.ddtassistant.type.auction;

import cn.xiejx.ddtassistant.utils.Util;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

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
     * 筛选条件
     */
    private List<String> filterConditionList;

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
        filterConditionList = auctionData.getFilterConditionList();
        autoAddUnknown = auctionData.getAutoAddUnknown();
    }

    public AuctionItem getItem(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        name = name.replaceAll("\\s+", "");
        if (CollectionUtils.isEmpty(auctionItemList)) {
            return null;
        }

        for (AuctionItem auctionItem : auctionItemList) {
            if (!Objects.equals(auctionItem.getOcrName(), name) && !Objects.equals(auctionItem.getName(), name)) {
                continue;
            }
            return auctionItem;
        }

        if (Boolean.TRUE.equals(autoAddUnknown)) {
            log.info("添加[{}]到列表", name);
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
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return mapper.readValue(s, AuctionData.class);
        } catch (Exception e) {
            log.warn("加载拍卖场自定义列表失败, {}", e.getMessage(), e);
        }
        return auctionData;
    }

    public void save() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
            Util.writeFile(result, AuctionConstants.FILENAME);
        } catch (Exception e) {
            log.warn("保存失败, {}", e.getMessage(), e);
        }
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
