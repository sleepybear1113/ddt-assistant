package cn.xiejx.ddtassistant.controller;

import cn.xiejx.ddtassistant.logic.DmLogic;
import cn.xiejx.ddtassistant.vo.MyString;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Arrays;
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
//        return Arrays.asList(1, 2, 3);
    }

    @RequestMapping("/dm/getGameScreenPath")
    public MyString getGameScreenPath(int hwnd) {
        return new MyString(dmLogic.getGameScreenPath(hwnd));
//        return new MyString("file/114.png");
    }

}
