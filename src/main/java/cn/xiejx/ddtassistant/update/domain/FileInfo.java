package cn.xiejx.ddtassistant.update.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2023/02/02 20:30
 */
@Data
public class FileInfo implements Serializable {
    private static final long serialVersionUID = 3883492306959472399L;

    private String filename;
    private String path;
    private String url;
    private String md5;
    private Long size;

    /**
     * 文件类型
     */
    private Integer type;

    private Integer updateStrategy;
}
