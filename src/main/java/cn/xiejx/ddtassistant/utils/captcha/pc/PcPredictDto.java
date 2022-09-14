package cn.xiejx.ddtassistant.utils.captcha.pc;

import cn.xiejx.ddtassistant.utils.captcha.BasePredictDto;
import cn.xiejx.ddtassistant.utils.captcha.BaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.message.BasicNameValuePair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/09/14 22:35
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
@NoArgsConstructor
public class PcPredictDto extends BasePredictDto implements Serializable {
    private static final long serialVersionUID = -3828083082254095381L;

    public PcPredictDto(String imgFile) {
        super();
        super.setImgFile(imgFile);
    }

    @Override
    public String getUrl() {
        return "http://139.155.237.55:21000/predict";
    }

    @Override
    public List<NameValuePair> buildPair() {
        String base64Img = imgToBase64();
        if (base64Img == null) {
            log.warn("图片转 base64 失败");
            return null;
        }
        ArrayList<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("file", base64Img));
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
        return (Class<T>) PcResponse.class;
    }
}
