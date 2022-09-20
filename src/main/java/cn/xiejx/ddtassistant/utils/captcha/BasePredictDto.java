package cn.xiejx.ddtassistant.utils.captcha;

import cn.xiejx.ddtassistant.base.CaptchaConfig;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;

import java.io.IOException;
import java.io.InputStream;
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
    private static final long serialVersionUID = 2686294956163943879L;

    private String imgFile;

    public BasePredictDto() {
    }

    /**
     * 获取打码平台 URL
     *
     * @return String
     */
    @JsonIgnore
    public abstract String getUrl();

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
     * 构建对应 BasePredictDto
     *
     * @param captchaConfig captchaConfig
     * @param imgFilePath   imgFilePath
     */
    public abstract void build(CaptchaConfig captchaConfig, String imgFilePath);

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
    public abstract boolean testConnection();

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
}
