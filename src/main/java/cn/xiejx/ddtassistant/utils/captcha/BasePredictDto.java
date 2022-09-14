package cn.xiejx.ddtassistant.utils.captcha;

import cn.xiejx.ddtassistant.utils.captcha.pc.PcResponse;
import lombok.Data;
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
 * @date 2022/09/14 22:50
 */
@Data
@Slf4j
public abstract class BasePredictDto implements Serializable {
    private static final long serialVersionUID = 2686294956163943879L;

    private String imgFile;

    public BasePredictDto() {
    }

    public abstract String getUrl();

    public abstract List<NameValuePair> buildPair();

    public abstract RequestConfig getRequestConfig();

    public abstract <T extends BaseResponse> Class<T> getResponseClass();

    public String imgToBase64() {
        if (this.imgFile == null) {
            return null;
        }
        InputStream in;
        byte[] data = null;
        //读取图片字节数组
        try {
            in = Files.newInputStream(Paths.get(this.imgFile));
            data = new byte[in.available()];
            int i = in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Base64.getEncoder().encodeToString(data);
    }
}
