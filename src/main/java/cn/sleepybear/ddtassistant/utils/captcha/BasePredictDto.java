package cn.sleepybear.ddtassistant.utils.captcha;

import cn.sleepybear.ddtassistant.base.CaptchaConfig;
import cn.sleepybear.ddtassistant.exception.FrontException;
import cn.sleepybear.ddtassistant.type.captcha.CaptchaConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serial;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/09/14 22:50
 */
@Data
@Slf4j
public abstract class BasePredictDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 2686294956163943879L;

    private String imgFile;

    private BaseCaptchaWay baseCaptchaWay;

    public BasePredictDto() {
    }

    /**
     * 获取服务器地址，如果没有可用的服务器地址，返回 null。<br>
     * 如果 checkConnection 为 false，则不会检查服务器连通性，直接返回第一个服务器地址。<br>
     * 如果 checkConnection 为 true，但是没有可用的服务器地址，则返回 null。<br>
     * @param checkConnection checkConnection
     * @return String
     */
    public String getServerUrl(boolean checkConnection) {
        List<String> serverAddressList = this.baseCaptchaWay.getServerAddressList();
        if (CollectionUtils.isEmpty(serverAddressList)) {
            throw new FrontException("服务器地址为空！请检查确认是否填写并且点击“保存打码设置”！");
        }

        String url = null;
        for (String serverAddress : serverAddressList) {
            String key = serverAddress + "@" + this.baseCaptchaWay.getId();
            Long t = CaptchaConstants.CAPTCHA_SERVER_CONNECT_FAIL_CACHER.get(key);
            if (t == null) {
                if (checkConnection) {
                    if (!testConnection(serverAddress)) {
                        // 测试连接失败，设置过期时间
                        CaptchaConstants.CAPTCHA_SERVER_CONNECT_FAIL_CACHER.set(key, System.currentTimeMillis(), CaptchaConstants.DEFAULT_EXPIRE_TIME);
                        continue;
                    }
                }
                url = serverAddress;
                break;
            }
        }

        return decryptHost(url);
    }

    /**
     * 获取打码平台 URL
     *
     * @return String
     */
    @JsonIgnore
    public abstract String getPredictUrl();

    /**
     * 构建打码请求体
     *
     * @return List<NameValuePair>
     */
    public abstract List<NameValuePair> buildPair();

    /**
     * 返回 RequestConfig
     *
     * @return RequestConfig
     */
    @JsonIgnore
    public abstract RequestConfig getRequestConfig();

    /**
     * 对应的 ResponseClass
     *
     * @param <T> BaseResponse
     * @return BaseResponse
     */
    @JsonIgnore
    public abstract <T extends BaseResponse> Class<T> getResponseClass();

    /**
     * 测试平台连通性
     *
     * @return boolean
     */
    public abstract boolean testConnection(String url);

    /**
     * 低余额提醒
     *
     * @param captchaConfig captchaConfig
     */
    public abstract void lowBalanceRemind(CaptchaConfig captchaConfig);

    /**
     * 获取账户信息
     *
     * @param captchaConfig captchaConfig
     * @return String
     */
    @JsonIgnore
    public abstract String getAccountInfo(CaptchaConfig captchaConfig);

    public String imgToBase64() {
        if (this.imgFile == null) {
            return null;
        }
        //读取图片字节数组
        try (InputStream in = Files.newInputStream(Paths.get(this.imgFile))) {
            byte[] data = new byte[in.available()];
            int i = in.read(data);
            return Base64.getEncoder().encodeToString(data);
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
            return null;
        }
    }

    public abstract String decryptHost(String host);
}
