package cn.sleepybear.ddtassistant.utils.captcha;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * There is description
 * @author sleepybear
 * @date 2023/09/20 22:23
 */
@Data
public class CaptchaInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 2383511506369213282L;

    private Integer id;

    /**
     * 验证码名称
     */
    private String captchaName;
    /**
     * 验证码类型，参考 {@link cn.sleepybear.ddtassistant.type.captcha.CaptchaConstants.CaptchaChoiceEnum}
     */
    private Integer captchaType;

    /**
     * 服务器地址
     */
    private List<String> serverAddressList;

    /**
     * 验证码参数<br>
     * 比如图鉴就是 第一个参数是用户名，第二个是密码，第三个是 softId，第四个是 typeId<br>
     * 平川打码就是 第一个参数是卡密，第二个是 author<br>
     */
    private List<String> params;

    /**
     * 设置可用打码的时间段，格式为：[[时(起),分(起),时(止),分(止)],[8,30,22,45]]
     */
    private List<List<Integer>> validTimeList;
}
