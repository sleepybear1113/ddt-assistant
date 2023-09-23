package cn.sleepybear.ddtassistant.update.vo;

import cn.sleepybear.ddtassistant.update.domain.FileInfo;
import cn.sleepybear.ddtassistant.update.constant.UpdateConstants;
import cn.sleepybear.ddtassistant.utils.Util;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.Serial;
import java.io.Serializable;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2023/02/03 16:16
 */
@Data
public class UpdateFileInfoVo implements Serializable {
    @Serial
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

    private Integer type;

    /**
     * 文件更新的策略
     */
    private Integer updateStrategy;

    /**
     * 文件的备注信息
     */
    private String info;

    private Boolean same;

    public static UpdateFileInfoVo build(FileInfo remoteFileInfo, String baseUrl) {
        UpdateFileInfoVo updateFileInfoVo = Util.copyBean(remoteFileInfo, UpdateFileInfoVo.class);
        updateFileInfoVo.buildUrl(baseUrl);
        updateFileInfoVo.setRemoteMd5(updateFileInfoVo.getMd5());
        updateFileInfoVo.setRemoteSize(updateFileInfoVo.getSize());

        updateFileInfoVo.buildSizeMd5();
        return updateFileInfoVo;
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
        if (this.url != null && StringUtils.isBlank(this.url)) {
            this.url = "";
            return;
        }

        this.url = baseUrl + this.path + this.filename;
    }

    public static UpdateFileInfoVo buildRemote(File file, String path) {
        if (file == null) {
            return null;
        }

        UpdateFileInfoVo updateFileInfoVo = new UpdateFileInfoVo();
        updateFileInfoVo.setFilename(file.getName());
        updateFileInfoVo.setMd5(Util.calcMd5(file));
        updateFileInfoVo.setSize(file.length());
        updateFileInfoVo.setType(UpdateConstants.TypeEnum.getTypeByFilename(file.getName()).getType());
        updateFileInfoVo.setUpdateStrategy(UpdateConstants.UpdateStrategyEnum.UPDATE_RECOMMEND.getType());
        String substring = file.getAbsolutePath().substring(path.length());
        updateFileInfoVo.setPath(substring.substring(0, substring.length() - file.getName().length()));
        return updateFileInfoVo;
    }

    public boolean deleteFile() {
        File file = new File(this.filename);
        if (file.exists()) {
            return file.delete();
        } else {
            return true;
        }
    }
}
