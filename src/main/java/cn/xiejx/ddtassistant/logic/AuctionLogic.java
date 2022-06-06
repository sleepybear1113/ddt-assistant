package cn.xiejx.ddtassistant.logic;

import cn.xiejx.ddtassistant.config.AuctionList;
import cn.xiejx.ddtassistant.dm.DmDdt;
import cn.xiejx.ddtassistant.exception.FrontException;
import cn.xiejx.ddtassistant.type.auction.Auction;
import cn.xiejx.ddtassistant.utils.Util;
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

    public Boolean bindAndSell(int hwnd) {
        boolean running = Auction.isRunning(hwnd);
        if (running) {
            return false;
        }
        return Auction.startSellThread(hwnd);
    }

    public Boolean stop(int hwnd) {
        boolean running = Auction.isRunning(hwnd);
        if (!running) {
            return false;
        }
        return Auction.stopAuction(hwnd);
    }
}
