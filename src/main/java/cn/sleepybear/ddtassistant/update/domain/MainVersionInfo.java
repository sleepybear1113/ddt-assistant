package cn.sleepybear.ddtassistant.update.domain;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2023/02/02 21:02
 */
@Data
public class MainVersionInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = -7870026818406318355L;

    /**
     * 工程的版本号，如 2.3.3, 2.4.5-beta-1 等
     */
    private String appVersion;

    /**
     * 数字的版本号，五位数数字，前 3 位代表程序版本，后 2 位代表类型版本。<br/>
     * 其中后 2 位需要与 5 做取余数，其中<br/>
     * 0 - 未使用<br/>
     * 1 - 内部版本<br/>
     * 2 - alpha 版测试版本<br/>
     * 3 - beta 版测试版本<br/>
     * 4 - 正式版本
     */
    private Integer version;
    private Integer versionType;

    /**
     * 这个版本更新文件的链接路径
     */
    private String updateMainFilePath;

    /**
     * 该版本的提示信息
     */
    private String info;
}
