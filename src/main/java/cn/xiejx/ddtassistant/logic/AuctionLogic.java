package cn.xiejx.ddtassistant.logic;

import cn.xiejx.ddtassistant.type.auction.AuctionData;
import cn.xiejx.ddtassistant.dm.DmDdt;
import cn.xiejx.ddtassistant.exception.FrontException;
import cn.xiejx.ddtassistant.type.auction.Auction;
import cn.xiejx.ddtassistant.vo.BindResultVo;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/06/04 19:32
 */
@Component
public class AuctionLogic {
    @Resource
    private AuctionData auctionData;
    @Resource
    private DmDdt defaultDm;

    public AuctionData getAuctionData() {
        return auctionData;
    }

    public void updateAuctionData(AuctionData auctionData) {
        if (auctionData == null) {
            throw new FrontException("数据为空");
        }

        this.auctionData.update(auctionData);
        this.auctionData.save();
    }

    public Boolean bindAndSell(int hwnd) {
        boolean running = Auction.isRunning(hwnd);
        if (running) {
            return false;
        }
        return Auction.startSellThread(hwnd);
    }

    public String bindAndSellAll(Integer[] hwnds) {
        if (hwnds == null || hwnds.length == 0) {
            throw new FrontException("未选择任何句柄！");
        }
        BindResultVo bindResultVo = new BindResultVo();
        for (Integer hwnd : hwnds) {
            boolean running = Auction.isRunning(hwnd);
            if (running) {
                bindResultVo.increaseRunningCount();
            } else {
                Auction.startSellThread(hwnd);
                bindResultVo.increaseNewAddCount();
            }
        }
        return bindResultVo.buildInfo();
    }

    public Boolean stop(int hwnd) {
        boolean running = Auction.isRunning(hwnd);
        if (!running) {
            return false;
        }
        return Auction.stopAuction(hwnd);
    }

    public Integer stopAll() {
        List<Integer> ddtWindowHwnd = getAllHwnds();
        int count = 0;
        for (Integer hwnd : ddtWindowHwnd) {
            count += stop(hwnd) ? 1 : 0;
        }
        return count;
    }

    public List<Integer> getAllHwnds() {
        List<Integer> ddtWindowHwnd = defaultDm.enumDdtWindowHwnd();
        if (CollectionUtils.isEmpty(ddtWindowHwnd)) {
            throw new FrontException("没有 flash 窗口");
        }
        return ddtWindowHwnd;
    }
}
