package cn.xiejx.ddtassistant.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2023/02/12 22:43
 */
@Data
public class FileInfoVo implements Serializable {
    private static final long serialVersionUID = 7095542481267689948L;

    private String filename;
    private String absoluteFilename;
    private Boolean isDir;
    private Long size;
}
