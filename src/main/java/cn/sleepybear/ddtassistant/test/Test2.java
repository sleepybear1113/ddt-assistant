package cn.sleepybear.ddtassistant.test;

import cn.sleepybear.ddtassistant.dm.DmDdt;
import cn.sleepybear.ddtassistant.utils.Util;
import lombok.extern.slf4j.Slf4j;

/**
 * @author sleepybear
 */
@Slf4j
public class Test2 {
    public static void main(String[] args) throws Exception {
        cap();
    }

    public static void cap() {
        System.out.println(Util.testAdmin());
        int[] region = new int[]{0, 0, 200, 200};
        int hwnd = 459448;
        DmDdt dm = DmDdt.createInstance(hwnd);
        dm.bind();
        Util.sleep(100L);
        dm.capturePicByRegion("test/cap/1.png", region);
        System.out.println("error-" + dm.getLastError());
        Integer address = dm.getScreenData(region);
        System.out.println("error-" + dm.getLastError());
        System.out.println(address);
        String s = dm.readData(hwnd, address, 40000);
        System.out.println(s);
        System.out.println("error-" + dm.getLastError());

        dm.getScreenDataBmp(region);
        System.out.println("error-" + dm.getLastError());
    }


}
