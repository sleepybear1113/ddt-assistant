package cn.xiejx.ddtassistant.logic;

import cn.xiejx.ddtassistant.config.AuctionList;
import cn.xiejx.ddtassistant.exception.FrontException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/06/04 19:32
 */
@Component
public class AuctionLogic {
    @Resource
    private AuctionList auctionList;

    public AuctionList getAuctionList() {
        return auctionList;
    }

    public void updateAuctionList(AuctionList auctionList) {
        if (auctionList == null) {
            throw new FrontException("数据为空");
        }

        this.auctionList.update(auctionList);
        this.auctionList.save();
    }
}
