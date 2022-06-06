package cn.xiejx.ddtassistant.controller;

import cn.xiejx.ddtassistant.config.AuctionList;
import cn.xiejx.ddtassistant.logic.AuctionLogic;
import cn.xiejx.ddtassistant.vo.MyString;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/06/04 19:32
 */
@RestController
public class AuctionController {
    @Resource
    private AuctionLogic auctionLogic;

    @RequestMapping("/auction/getList")
    public AuctionList getAuctionList() {
        return auctionLogic.getAuctionList();
    }

    @RequestMapping("/auction/update")
    public Boolean update(@RequestBody AuctionList auctionList) {
        auctionLogic.updateAuctionList(auctionList);
        return true;
    }

    @RequestMapping("/auction/bindAndSell")
    public Boolean bindAndSell(int hwnd) {
        return auctionLogic.bindAndSell(hwnd);
    }

    @RequestMapping("/auction/bindAndSellAll")
    public MyString bindAndSell(Integer[] hwnds) {
        return new MyString(auctionLogic.bindAndSellAll(hwnds));
    }

    @RequestMapping("/auction/stop")
    public Boolean stop(int hwnd) {
        return auctionLogic.stop(hwnd);
    }

    @RequestMapping("/auction/stopAll")
    public Integer stopAll() {
        return auctionLogic.stopAll();
    }
}
