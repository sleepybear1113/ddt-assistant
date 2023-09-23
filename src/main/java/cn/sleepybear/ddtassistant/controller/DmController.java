package cn.sleepybear.ddtassistant.controller;

import cn.sleepybear.ddtassistant.logic.DmLogic;
import cn.sleepybear.ddtassistant.vo.MyString;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/06/06 15:17
 */
@RestController
public class DmController {
    @Resource
    private DmLogic dmLogic;

    @RequestMapping("/dm/getDdtHwnds")
    public List<Integer> getDdtHwnds() {
        return dmLogic.getDdtHwnds();
    }

    @RequestMapping("/dm/getGameScreenPath")
    public MyString getGameScreenPath(int hwnd, Integer imgQuality) {
        return new MyString(dmLogic.getGameScreenPath(hwnd, imgQuality));
    }

}
