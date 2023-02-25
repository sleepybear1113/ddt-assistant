package cn.xiejx.ddtassistant.vo;

import lombok.Data;

import java.io.File;
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
    private Long lastModified;

    public static FileInfoVo build(File file) {
        if (!file.exists()) {
            return null;
        }

        FileInfoVo fileInfoVo = new FileInfoVo();
        fileInfoVo.setSize(file.length());
        fileInfoVo.setLastModified(file.lastModified());
        fileInfoVo.setFilename(file.getName());
        fileInfoVo.setIsDir(file.isDirectory());
        fileInfoVo.setAbsoluteFilename(fileInfoVo.absoluteFilename);
        return fileInfoVo;
    }
}
