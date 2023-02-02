package cn.xiejx.ddtassistant.update.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2023/02/02 21:02
 */
@Data
public class MainVersionInfo implements Serializable {
    private static final long serialVersionUID = -7870026818406318355L;

    private String appVersion;
    private Integer version;
    private String updateMainFilePath;
    private Integer type;
    private String info;
}
