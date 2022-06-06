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

    public static void press(String fn) {
        if (StringUtils.isBlank(fn)) {
            return;
        }
        Integer code = getCode(fn.toLowerCase());
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
            case "f1":
                code = KeyEvent.VK_F1;
                break;
            case "f2":
                code = KeyEvent.VK_F2;
                break;
            case "f3":
                code = KeyEvent.VK_F3;
                break;
            case "f4":
                code = KeyEvent.VK_F4;
                break;
            case "f5":
                code = KeyEvent.VK_F5;
                break;
            case "f6":
                code = KeyEvent.VK_F6;
                break;
            case "f7":
                code = KeyEvent.VK_F7;
                break;
            case "f8":
                code = KeyEvent.VK_F8;
                break;
            case "f9":
                code = KeyEvent.VK_F9;
                break;
            case "f10":
                code = KeyEvent.VK_F10;
                break;
            case "f11":
                code = KeyEvent.VK_F11;
                break;
            case "f12":
                code = KeyEvent.VK_F12;
                break;
            case "a":
                code = KeyEvent.VK_A;
                break;
            case "b":
                code = KeyEvent.VK_B;
                break;
            case "c":
                code = KeyEvent.VK_C;
                break;
            case "d":
                code = KeyEvent.VK_D;
                break;
            case "e":
                code = KeyEvent.VK_E;
                break;
            case "f":
                code = KeyEvent.VK_F;
                break;
            case "g":
                code = KeyEvent.VK_G;
                break;
            case "h":
                code = KeyEvent.VK_H;
                break;
            case "i":
                code = KeyEvent.VK_I;
                break;
            case "j":
                code = KeyEvent.VK_J;
                break;
            case "k":
                code = KeyEvent.VK_K;
                break;
            case "l":
                code = KeyEvent.VK_L;
                break;
            case "m":
                code = KeyEvent.VK_M;
                break;
            case "n":
                code = KeyEvent.VK_N;
                break;
            case "o":
                code = KeyEvent.VK_O;
                break;
            case "p":
                code = KeyEvent.VK_P;
                break;
            case "q":
                code = KeyEvent.VK_Q;
                break;
            case "r":
                code = KeyEvent.VK_R;
                break;
            case "s":
                code = KeyEvent.VK_S;
                break;
            case "t":
                code = KeyEvent.VK_T;
                break;
            case "u":
                code = KeyEvent.VK_U;
                break;
            case "v":
                code = KeyEvent.VK_V;
                break;
            case "w":
                code = KeyEvent.VK_W;
                break;
            case "x":
                code = KeyEvent.VK_X;
                break;
            case "y":
                code = KeyEvent.VK_Y;
                break;
            case "z":
                code = KeyEvent.VK_Z;
                break;
            case "`":
                code = KeyEvent.VK_BACK_QUOTE;
                break;
            case "0":
                code = KeyEvent.VK_0;
                break;
            case "1":
                code = KeyEvent.VK_1;
                break;
            case "2":
                code = KeyEvent.VK_2;
                break;
            case "3":
                code = KeyEvent.VK_3;
                break;
            case "4":
                code = KeyEvent.VK_4;
                break;
            case "5":
                code = KeyEvent.VK_5;
                break;
            case "6":
                code = KeyEvent.VK_6;
                break;
            case "7":
                code = KeyEvent.VK_7;
                break;
            case "8":
                code = KeyEvent.VK_8;
                break;
            case "9":
                code = KeyEvent.VK_9;
                break;
            case "-":
                code = KeyEvent.VK_MINUS;
                break;
            case "=":
                code = KeyEvent.VK_EQUALS;
                break;
            case "!":
                code = KeyEvent.VK_EXCLAMATION_MARK;
                break;
            case "@":
                code = KeyEvent.VK_AT;
                break;
            case "#":
                code = KeyEvent.VK_NUMBER_SIGN;
                break;
            case "$":
                code = KeyEvent.VK_DOLLAR;
                break;
            case "^":
                code = KeyEvent.VK_CIRCUMFLEX;
                break;
            case "&":
                code = KeyEvent.VK_AMPERSAND;
                break;
            case "*":
                code = KeyEvent.VK_ASTERISK;
                break;
            case "(":
                code = KeyEvent.VK_LEFT_PARENTHESIS;
                break;
            case ")":
                code = KeyEvent.VK_RIGHT_PARENTHESIS;
                break;
            case "_":
                code = KeyEvent.VK_UNDERSCORE;
                break;
            case "+":
                code = KeyEvent.VK_PLUS;
                break;
            case "\t":
                code = KeyEvent.VK_TAB;
                break;
            case "\n":
                code = KeyEvent.VK_ENTER;
                break;
            case "[":
                code = KeyEvent.VK_OPEN_BRACKET;
                break;
            case "]":
                code = KeyEvent.VK_CLOSE_BRACKET;
                break;
            case "\\":
                code = KeyEvent.VK_BACK_SLASH;
                break;
            case ";":
                code = KeyEvent.VK_SEMICOLON;
                break;
            case ":":
                code = KeyEvent.VK_COLON;
                break;
            case "'":
                code = KeyEvent.VK_QUOTE;
                break;
            case "\"":
                code = KeyEvent.VK_QUOTEDBL;
                break;
            case ",":
                code = KeyEvent.VK_COMMA;
                break;
            case ".":
                code = KeyEvent.VK_PERIOD;
                break;
            case "/":
                code = KeyEvent.VK_SLASH;
                break;
            case " ":
                code = KeyEvent.VK_SPACE;
                break;
            case "space":
                code = KeyEvent.VK_SPACE;
                break;
            case "back":
                code = KeyEvent.VK_BACK_SPACE;
                break;
            case "enter":
                code = KeyEvent.VK_ENTER;
                break;
            case "tab":
                code = KeyEvent.VK_TAB;
                break;
            case "ctrl":
                code = KeyEvent.VK_CONTROL;
                break;
            case "alt":
                code = KeyEvent.VK_ALT;
                break;
            case "win":
                code = KeyEvent.VK_OPEN_BRACKET;
                break;
            case "esc":
                code = KeyEvent.VK_ESCAPE;
                break;
            case "delete":
                code = KeyEvent.VK_DELETE;
                break;
            case "up":
                code = KeyEvent.VK_UP;
                break;
            case "down":
                code = KeyEvent.VK_DOWN;
                break;
            case "left":
                code = KeyEvent.VK_LEFT;
                break;
            case "right":
                code = KeyEvent.VK_RIGHT;
                break;
            case "home":
                code = KeyEvent.VK_HOME;
                break;
            case "end":
                code = KeyEvent.VK_END;
                break;
            case "pgup":
                code = KeyEvent.VK_PAGE_UP;
                break;
            case "pgdn":
                code = KeyEvent.VK_PAGE_DOWN;
                break;
            case "option":
                code = KeyEvent.VK_CLOSE_BRACKET;
                break;
        }
        return code;
    }
}
