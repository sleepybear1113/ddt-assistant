package cn.xiejx.ddtassistant.update.vo;

import cn.xiejx.ddtassistant.update.constant.UpdateConstants;
import cn.xiejx.ddtassistant.update.domain.FileInfo;
import cn.xiejx.ddtassistant.utils.Util;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
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

    private Integer id;

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
    private String remoteMd5;

    /**
     * 文件大小
     */
    private Long size;
    private Long remoteSize;

    /**
     * 文件更新的策略
     */
    private Integer updateStrategy;

    /**
     * 文件的备注信息
     */
    private String info;

    private Boolean same;

    public static FileInfoVo build(FileInfo remoteFileInfo, String baseUrl) {
        FileInfoVo fileInfoVo = Util.copyBean(remoteFileInfo, FileInfoVo.class);
        fileInfoVo.buildUrl(baseUrl);
        fileInfoVo.setRemoteMd5(fileInfoVo.getMd5());
        fileInfoVo.setRemoteSize(fileInfoVo.getSize());

        fileInfoVo.buildSizeMd5();
        return fileInfoVo;
    }

    public void buildSizeMd5() {
        File file = new File(path + filename);
        if (!file.exists()) {
            size = 0L;
            md5 = null;
        } else {
            size = file.length();
            md5 = Util.calcMd5(file);
        }

        if (md5 == null && remoteMd5 == null) {
            same = true;
        } else {
            if (md5 == null) {
                same = false;
            } else {
                same = md5.equals(remoteMd5);
            }
        }
    }

    public void buildUrl(String baseUrl) {
        if (StringUtils.isNotBlank(this.url)) {
            return;
        }

        this.url = baseUrl + this.path + this.filename;
    }

    public static FileInfoVo buildRemote(File file, String path) {
        if (file == null) {
            return null;
        }

        FileInfoVo fileInfoVo = new FileInfoVo();
        fileInfoVo.setFilename(file.getName());
        fileInfoVo.setMd5(Util.calcMd5(file));
        fileInfoVo.setSize(file.length());
        fileInfoVo.setUpdateStrategy(UpdateConstants.updateStrategyEnum.UPDATE_RECOMMEND.getType());
        String substring = file.getAbsolutePath().substring(path.length());
        fileInfoVo.setPath(substring.substring(0, substring.length() - file.getName().length()));
        return fileInfoVo;
    }
}
