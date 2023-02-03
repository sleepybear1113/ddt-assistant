package cn.xiejx.ddtassistant.update.vo;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2023/02/03 16:16
 */
@Data
public class FileInfoVo implements Serializable {
    private static final long serialVersionUID = 3904341239457981111L;

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
     * 文件类型
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

    public void build(String baseUrl) {
        buildUrl(baseUrl);
    }

    public void buildUrl(String baseUrl) {
        if (StringUtils.isBlank(this.url)) {
            return;
        }

        if (this.url.startsWith("http")) {
            return;
        }

        this.url = baseUrl + this.url;
    }
}
