package cn.xiejx.ddtassistant.utils;

import cn.xiejx.ddtassistant.exception.FrontException;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * @author sleepybear
 */
public class KeyPadUtil {

    private static Robot robot;

    static {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public static void pressFn(String fn) {
        if (StringUtils.isBlank(fn)) {
            return;
        }
        Integer code = getCode(fn);
        if (code == null) {
            throw new FrontException("暂不支持按下 " + fn);
        }
        keyPress(code);
    }

    public static void keyPress(int code) {
        keyPress(code, 50L);
    }

    public static void keyPress(int code, Long delay) {
        keyDown(code);
        Util.sleep(delay);
        keyUp(code);
    }

    public static void keyDown(int code) {
        robot.keyPress(code);
    }

    public static void keyUp(int code) {
        robot.keyRelease(code);
    }

    public static Integer getCode(String key) {
        Integer code = null;
        switch (key) {
            case "F1":
                code = KeyEvent.VK_F1;
                break;
            case "F2":
                code = KeyEvent.VK_F2;
                break;
            case "F3":
                code = KeyEvent.VK_F3;
                break;
            case "F4":
                code = KeyEvent.VK_F4;
                break;
            case "F5":
                code = KeyEvent.VK_F5;
                break;
            case "F6":
                code = KeyEvent.VK_F6;
                break;
            case "F7":
                code = KeyEvent.VK_F7;
                break;
            case "F8":
                code = KeyEvent.VK_F8;
                break;
            case "F9":
                code = KeyEvent.VK_F9;
                break;
            case "F10":
                code = KeyEvent.VK_F10;
                break;
            case "F11":
                code = KeyEvent.VK_F11;
                break;
            case "F12":
                code = KeyEvent.VK_F12;
        }
        return code;
    }
}
