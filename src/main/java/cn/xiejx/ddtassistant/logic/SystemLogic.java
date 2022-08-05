package cn.xiejx.ddtassistant.logic;

import cn.xiejx.ddtassistant.constant.Constants;
import cn.xiejx.ddtassistant.constant.GlobalVariable;
import cn.xiejx.ddtassistant.exception.FrontException;
import cn.xiejx.ddtassistant.utils.Util;
import cn.xiejx.ddtassistant.vo.MyString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/06/05 21:13
 */
@Component
@Slf4j
public class SystemLogic {

    public void openWithExplorer(String path, boolean select) {
        Util.openWithExplorer(path, select);
    }

    public MyString getHost() {
        String hostFilename = getHostFilename();
        if (StringUtils.isBlank(hostFilename)) {
            return new MyString("");
        }
        String s = Util.readFile(hostFilename);
//        updateHost(s);
        return new MyString(s);
    }

    public Boolean updateHost(String host) {
        if (!GlobalVariable.isAdmin) {
            throw new FrontException("没有管理员权限，无法修改 host 文件");
        }
        String hostFilename = getHostFilename();
        if (StringUtils.isBlank(hostFilename)) {
            hostFilename = Constants.HOSTS_NORMAL_FILENAME;
        }
        Util.writeFile(host, hostFilename);
        return true;
    }

    public static String getHostFilename() {
        String hostFilename = "";
        if (new File(Constants.HOSTS_ICS_FILENAME).exists()) {
            hostFilename = Constants.HOSTS_ICS_FILENAME;
        } else if (new File(Constants.HOSTS_NORMAL_FILENAME).exists()) {
            hostFilename = Constants.HOSTS_NORMAL_FILENAME;
        }
        return hostFilename;
    }
}
