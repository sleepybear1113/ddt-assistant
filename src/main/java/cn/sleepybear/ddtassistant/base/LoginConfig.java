package cn.sleepybear.ddtassistant.base;

import jakarta.servlet.http.Cookie;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serial;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2023/02/02 14:25
 */
@Data
public class LoginConfig implements Serializable {
    @Serial
    private static final long serialVersionUID = -5192850585912872638L;

    public static final String SPLIT = "#_:_%";
    public static final String COOKIE_NAME = "user";
    public static final int COOKIE_MAX_VALID_TIME = Integer.MAX_VALUE;

    private Boolean enableLogin;
    private String username;
    private String password;

    public boolean login(String base64) {
        if (StringUtils.isBlank(base64)) {
            return false;
        }
        String s = new String(Base64.getDecoder().decode(base64));
        if (!s.contains(SPLIT)) {
            return false;
        }

        String[] split = s.split(SPLIT);
        return login(split[0], split.length == 1 ? null : split[1]);
    }

    public boolean login(String username, String password) {
        if (!Boolean.TRUE.equals(enableLogin)) {
            return true;
        }
        if (this.username == null) {
            this.username = "";
        }
        if (this.password == null) {
            this.password = "";
        }
        if (username == null) {
            username = "";
        }
        if (password == null) {
            password = "";
        }

        return this.username.equals(username) && this.password.equals(password);
    }

    public String toBase64() {
        return Base64.getEncoder().encodeToString((username + SPLIT + password + SPLIT).getBytes(StandardCharsets.UTF_8));
    }

    public Cookie loginSuccessCookie() {
        Cookie cookie = new Cookie(COOKIE_NAME, toBase64());
        cookie.setMaxAge(COOKIE_MAX_VALID_TIME);
        return cookie;
    }

    public static Cookie loginExpireCookie() {
        Cookie cookie = new Cookie(COOKIE_NAME, "");
        cookie.setMaxAge(0);
        return cookie;
    }
}
