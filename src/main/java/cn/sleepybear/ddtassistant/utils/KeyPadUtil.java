package cn.sleepybear.ddtassistant.utils;

import cn.sleepybear.ddtassistant.exception.FrontException;
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

    public static void press(String key) {
        if (StringUtils.isBlank(key)) {
            return;
        }
        Integer code = getCode(key.toLowerCase());
        if (code == null) {
            throw new FrontException("暂不支持按下 " + key);
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
        return switch (key) {
            case "f1" -> KeyEvent.VK_F1;
            case "f2" -> KeyEvent.VK_F2;
            case "f3" -> KeyEvent.VK_F3;
            case "f4" -> KeyEvent.VK_F4;
            case "f5" -> KeyEvent.VK_F5;
            case "f6" -> KeyEvent.VK_F6;
            case "f7" -> KeyEvent.VK_F7;
            case "f8" -> KeyEvent.VK_F8;
            case "f9" -> KeyEvent.VK_F9;
            case "f10" -> KeyEvent.VK_F10;
            case "f11" -> KeyEvent.VK_F11;
            case "f12" -> KeyEvent.VK_F12;
            case "a" -> KeyEvent.VK_A;
            case "b" -> KeyEvent.VK_B;
            case "c" -> KeyEvent.VK_C;
            case "d" -> KeyEvent.VK_D;
            case "e" -> KeyEvent.VK_E;
            case "f" -> KeyEvent.VK_F;
            case "g" -> KeyEvent.VK_G;
            case "h" -> KeyEvent.VK_H;
            case "i" -> KeyEvent.VK_I;
            case "j" -> KeyEvent.VK_J;
            case "k" -> KeyEvent.VK_K;
            case "l" -> KeyEvent.VK_L;
            case "m" -> KeyEvent.VK_M;
            case "n" -> KeyEvent.VK_N;
            case "o" -> KeyEvent.VK_O;
            case "p" -> KeyEvent.VK_P;
            case "q" -> KeyEvent.VK_Q;
            case "r" -> KeyEvent.VK_R;
            case "s" -> KeyEvent.VK_S;
            case "t" -> KeyEvent.VK_T;
            case "u" -> KeyEvent.VK_U;
            case "v" -> KeyEvent.VK_V;
            case "w" -> KeyEvent.VK_W;
            case "x" -> KeyEvent.VK_X;
            case "y" -> KeyEvent.VK_Y;
            case "z" -> KeyEvent.VK_Z;
            case "`" -> KeyEvent.VK_BACK_QUOTE;
            case "0" -> KeyEvent.VK_0;
            case "1" -> KeyEvent.VK_1;
            case "2" -> KeyEvent.VK_2;
            case "3" -> KeyEvent.VK_3;
            case "4" -> KeyEvent.VK_4;
            case "5" -> KeyEvent.VK_5;
            case "6" -> KeyEvent.VK_6;
            case "7" -> KeyEvent.VK_7;
            case "8" -> KeyEvent.VK_8;
            case "9" -> KeyEvent.VK_9;
            case "-" -> KeyEvent.VK_MINUS;
            case "=" -> KeyEvent.VK_EQUALS;
            case "!" -> KeyEvent.VK_EXCLAMATION_MARK;
            case "@" -> KeyEvent.VK_AT;
            case "#" -> KeyEvent.VK_NUMBER_SIGN;
            case "$" -> KeyEvent.VK_DOLLAR;
            case "^" -> KeyEvent.VK_CIRCUMFLEX;
            case "&" -> KeyEvent.VK_AMPERSAND;
            case "*" -> KeyEvent.VK_ASTERISK;
            case "(" -> KeyEvent.VK_LEFT_PARENTHESIS;
            case ")" -> KeyEvent.VK_RIGHT_PARENTHESIS;
            case "_" -> KeyEvent.VK_UNDERSCORE;
            case "+" -> KeyEvent.VK_PLUS;
            case "\t" -> KeyEvent.VK_TAB;
            case "\n" -> KeyEvent.VK_ENTER;
            case "[" -> KeyEvent.VK_OPEN_BRACKET;
            case "]" -> KeyEvent.VK_CLOSE_BRACKET;
            case "\\" -> KeyEvent.VK_BACK_SLASH;
            case ";" -> KeyEvent.VK_SEMICOLON;
            case ":" -> KeyEvent.VK_COLON;
            case "'" -> KeyEvent.VK_QUOTE;
            case "\"" -> KeyEvent.VK_QUOTEDBL;
            case "," -> KeyEvent.VK_COMMA;
            case "." -> KeyEvent.VK_PERIOD;
            case "/" -> KeyEvent.VK_SLASH;
            case " " -> KeyEvent.VK_SPACE;
            case "space" -> KeyEvent.VK_SPACE;
            case "back" -> KeyEvent.VK_BACK_SPACE;
            case "enter" -> KeyEvent.VK_ENTER;
            case "tab" -> KeyEvent.VK_TAB;
            case "ctrl" -> KeyEvent.VK_CONTROL;
            case "alt" -> KeyEvent.VK_ALT;
            case "win" -> KeyEvent.VK_OPEN_BRACKET;
            case "esc" -> KeyEvent.VK_ESCAPE;
            case "delete" -> KeyEvent.VK_DELETE;
            case "up" -> KeyEvent.VK_UP;
            case "down" -> KeyEvent.VK_DOWN;
            case "left" -> KeyEvent.VK_LEFT;
            case "right" -> KeyEvent.VK_RIGHT;
            case "home" -> KeyEvent.VK_HOME;
            case "end" -> KeyEvent.VK_END;
            case "pgup" -> KeyEvent.VK_PAGE_UP;
            case "pgdn" -> KeyEvent.VK_PAGE_DOWN;
            case "option" -> KeyEvent.VK_CLOSE_BRACKET;
            default -> null;
        };
    }
}
