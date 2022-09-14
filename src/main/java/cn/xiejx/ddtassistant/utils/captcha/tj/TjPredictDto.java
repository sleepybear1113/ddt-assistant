package cn.xiejx.ddtassistant.utils.captcha.tj;

import cn.xiejx.ddtassistant.base.UserConfig;
import cn.xiejx.ddtassistant.exception.FrontException;
import cn.xiejx.ddtassistant.utils.captcha.BasePredictDto;
import cn.xiejx.ddtassistant.utils.captcha.BaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/05/10 09:55
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class TjPredictDto extends BasePredictDto implements Serializable {
    private static final long serialVersionUID = 4340261310989122096L;
    private String username;
    private String password;
    private String typeId;
    private String typeName;
    private String softId;

    public TjPredictDto() {
    }

    @Override
    public String getUrl() {
        return "http://api.ttshitu.com/predict";
    }

    public TjPredictDto(String username, String password, String typeId, String typeName, String softId, String imgFile) {
        super();
        super.setImgFile(imgFile);
        this.username = username;
        this.password = password;
        this.typeId = typeId;
        this.typeName = typeName;
        this.softId = softId;
    }

    public static TjPredictDto build(UserConfig userConfig, String path) {
        if (userConfig == null) {
            throw new FrontException("用户信息不存在");
        }

        String username = userConfig.getUsername();
        String password = userConfig.getPassword();
        if (username == null || username.length() == 0 || password == null || password.length() == 0) {
            throw new FrontException("用户名密码缺失");
        }

        return new TjPredictDto(username, password, userConfig.getTypeId(), "", userConfig.getSoftId(), path);
    }

    @Override
    public List<NameValuePair> buildPair() {
        String base64Img = imgToBase64();
        if (base64Img == null) {
            log.warn("图片转 base64 失败");
            return null;
        }
        ArrayList<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("username", this.username));
        pairs.add(new BasicNameValuePair("password", this.password));
        pairs.add(new BasicNameValuePair("typeid", this.typeId));
        pairs.add(new BasicNameValuePair("typename", this.typeName));
        pairs.add(new BasicNameValuePair("softid", this.softId));
        pairs.add(new BasicNameValuePair("image", base64Img));
        return pairs;
    }

    @Override
    public RequestConfig getRequestConfig() {
        return RequestConfig.custom()
                .setConnectionRequestTimeout(1000 * 5)
                .setConnectTimeout(1000 * 5)
                .setSocketTimeout(1000 * 62)
                .build();
    }

    @Override
    public <T extends BaseResponse> Class<T> getResponseClass() {
        return (Class<T>) TjResponse.class;
    }
}
