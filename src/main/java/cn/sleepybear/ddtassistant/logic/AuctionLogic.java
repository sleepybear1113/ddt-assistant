package cn.sleepybear.ddtassistant.logic;

import cn.sleepybear.ddtassistant.base.SettingConfig;
import cn.sleepybear.ddtassistant.dm.DmDdt;
import cn.sleepybear.ddtassistant.exception.FrontException;
import cn.sleepybear.ddtassistant.type.auction.Auction;
import cn.sleepybear.ddtassistant.type.auction.AuctionData;
import cn.sleepybear.ddtassistant.vo.BindResultVo;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
    @Resource
    private SettingConfig settingConfig;

    public AuctionData getAuctionData() {
        this.auctionData.update(AuctionData.load());
        return this.auctionData;
    }

    public void updateAuctionData(AuctionData auctionData) {
        if (auctionData == null) {
            throw new FrontException("数据为空");
        }

        auctionData.setFilterConditionList(this.auctionData.getFilterConditionList());
        this.auctionData.update(auctionData);
        this.auctionData.save();
    }

    public Boolean bindAndSell(int hwnd, boolean confirm) {
        boolean running = Auction.isRunning(hwnd, Auction.class);
        if (running) {
            return false;
        }
        return Auction.startSellThread(hwnd, confirm, settingConfig.getBindWindowConfig());
    }

    public String bindAndSellAll(Integer[] hwnds, boolean confirm) {
        if (hwnds == null || hwnds.length == 0) {
            throw new FrontException("未选择任何句柄！");
        }
        BindResultVo bindResultVo = new BindResultVo();
        for (Integer hwnd : hwnds) {
            boolean running = Auction.isRunning(hwnd, Auction.class);
            if (running) {
                bindResultVo.increaseRunningCount();
            } else {
                Auction.startSellThread(hwnd, confirm, settingConfig.getBindWindowConfig());
                bindResultVo.increaseNewAddCount();
            }
        }
        return bindResultVo.buildInfo();
    }

    public Boolean stop(int hwnd) {
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

    public void updateFilterCondition(List<String> filter) {
        this.auctionData.setFilterConditionList(filter);
        updateAuctionData(this.auctionData);
    }

    public List<String> getFilterCondition() {
        return CollectionUtils.isEmpty(this.auctionData.getFilterConditionList()) ? new ArrayList<>() : this.auctionData.getFilterConditionList();
    }
}
