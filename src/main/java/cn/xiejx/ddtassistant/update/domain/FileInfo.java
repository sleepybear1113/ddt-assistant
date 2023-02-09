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

    /**
     * 文件名
     */
    private String filename;

    /**
     * 文件的本地相对路径
     */
    private String path;

    /**
     * 获取文件的链接地址
     */
    private String url;

    /**
     * 文件的 md5
     */
    private String md5;

    /**
     * 文件大小
     */
    private Long size;

    /**
     * 文件类型，是否文本
     */
    private Integer type;

    /**
     * 文件更新的策略
     */
    private Integer updateStrategy;

    /**
     * 文件的备注信息
     */
    private String info;
}
