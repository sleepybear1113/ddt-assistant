package cn.xiejx.ddtassistant.utils.tj;

import cn.xiejx.ddtassistant.config.UserConfig;
import cn.xiejx.ddtassistant.exception.FrontException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
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
@Data
@Slf4j
public class TjPredictDto implements Serializable {
    private static final long serialVersionUID = 4340261310989122096L;
    private String username;
    private String password;
    private String typeId;
    private String typeName;
    private String softId;
    private String imgFile;

    public TjPredictDto() {
    }

    public TjPredictDto(String username, String password, String typeId, String typeName, String softId, String imgFile) {
        this.username = username;
        this.password = password;
        this.typeId = typeId;
        this.typeName = typeName;
        this.softId = softId;
        this.imgFile = imgFile;
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

       return new TjPredictDto(username, password, "7", "", userConfig.getSoftId(), path);
    }

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
