package cn.xiejx.ddtassistant.update.domain;

import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.io.File;
import java.io.Serializable;
import java.util.List;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2023/02/02 20:50
 */
@Data
public class UpdateList implements Serializable {
    private static final long serialVersionUID = -7054493044641447939L;

    private String appVersion;
    private Integer version;

    private String baseUrl;
    private List<FileInfo> statics;
}
