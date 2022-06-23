package cn.xiejx.ddtassistant.controller2;

import cn.xiejx.ddtassistant.utils.Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Arrays;

/**
 * @author sleepybear
 */
@RestController
@Slf4j
public class ProxyController {
    private static final String CARD_TYPE = "八开全套";

    @RequestMapping("/ocr2")
    public String proxyOcr2(HttpServletRequest request) throws IOException {
        int contentLength = request.getContentLength();

        byte[] bytesFromRequest = new byte[contentLength];
        DataInputStream dataInputStream = new DataInputStream(request.getInputStream());
        dataInputStream.readFully(bytesFromRequest);
        dataInputStream.close();

        Resource resource = new ClassPathResource("sample/zj-pic.png");
        long length = resource.contentLength();
        InputStream is = resource.getInputStream();
        byte[] bytesFromResource = new byte[(int) length];
        int read = is.read(bytesFromResource);

        boolean equals = Arrays.equals(bytesFromRequest, bytesFromResource);
        if (equals) {
            log.info("验证码服务器启动测试请求捕获！");
            return "A";
        }

        log.info("验证码图片请求捕获，延迟 5 秒返回空答案！");
        Util.sleep(5000L);
        return "";
    }

    @RequestMapping("/v1/card/login")
    public CardLogin login(String app_key, String card, String device_id, String nonce, String timestamp, String sign) {
        return CardLogin.build();
    }

    @RequestMapping("/v1/card/config")
    public String config() {
        return String.format("{\"code\":0,\"message\":\"ok\",\"result\":{\"config\":\"%s\"},\"nonce\":\"c6do74jdqusp4iu51rr0\",\"sign\":\"b4e00ce1addc305ab9f141e4c517f562\"}", CARD_TYPE);
    }

    @RequestMapping("/v1/card/heartbeat")
    public String heart() {
        return String.format("{\"code\":0,\"message\":\"ok\",\"result\":{\"expires\":\"2099-12-31 23:59:59\",\"expires_ts\":4102415999,\"server_time\":%s}", System.currentTimeMillis() / 1000);
    }

    @RequestMapping("/v1/af/remote_var")
    public String remoteVar() {
        return "{\"code\":0,\"message\":\"ok\",\"result\":{\"value\":\"41541-00000123\"},\"nonce\":\"ca2cqggo3pjbikipgj70\",\"sign\":\"9138257fd0f479079873fdb686fb6db9\"}";
    }

    @RequestMapping("/v1/af/remote_data")
    public String remoteData() {
        return "{\"code\":10522,\"message\":\"远程数据已存在\",\"nonce\":\"ca2c2jgo3pjbikic11qg\",\"sign\":\"fbdf9c90c2194f3670103d338ddb2df2\"}";
    }

    @RequestMapping("/v1/af/call_remote_func2")
    public String func2(HttpServletRequest request, String app_key, String func_name, String params, String nonce, String timestamp, String sign) {
        if (StringUtils.isBlank(params) ) {
            log.warn("call_remote_func2 失败，参数为空");
            return "0";
        }

        String[] split = params.replace("[", "").replace("]", "").split(",");
        if (split.length != 3) {
            log.warn("call_remote_func2 失败，参数：{}", params);
            return "0";
        }
        BigDecimal a = new BigDecimal(split[0]);
        BigDecimal b = new BigDecimal(split[1]);
        BigDecimal c = new BigDecimal(split[2]);
        BigDecimal m = new BigDecimal("1.008");
        String s = "{\"code\":0,\"message\":\"ok\",\"result\":{\"ret\":\"{\\\"ret\\\":%s}\"},\"nonce\":\"ca2c2jgo3pjbikic11qg\",\"sign\":\"fbdf9c90c2194f3670103d338ddb2df2\"}";
        String s1 = String.valueOf(a.doubleValue() * b.doubleValue() / c.doubleValue() / m.doubleValue());
        return String.format(s, s1);
    }

    static class CardLogin {
        public int code;
        public String message;
        public String nonce;
        public String sign;
        public CardLoginResult result;

        public CardLogin(int code, String message, String nonce, String sign) {
            this.code = code;
            this.message = message;
            this.nonce = nonce;
            this.sign = sign;
        }

        public static CardLogin build() {
            CardLogin cardLogin = new CardLogin(0, "ok", "c6do74bdqusp4iu51pgg", "e7a16af93887feaf284a80c010f8b0ac");
            cardLogin.result = new CardLoginResult("月卡", "BKoHy32HrrKg7W8sB8Qr", "2099-12-31 23:59:59", 4102415999000L, CARD_TYPE, System.currentTimeMillis() / 1000);
            return cardLogin;
        }
    }

    static class CardLoginResult {
        public String card_type;
        public String token;
        public String expires;
        public long expires_ts;
        public String config;
        public long server_time;

        public CardLoginResult(String card_type, String token, String expires, Long expires_ts, String config, Long server_time) {
            this.card_type = card_type;
            this.token = token;
            this.expires = expires;
            this.expires_ts = expires_ts;
            this.config = config;
            this.server_time = server_time;
        }
    }

}
