package cn.xiejx.ddtassistant.logic;

import cn.xiejx.ddtassistant.constant.Constants;
import cn.xiejx.ddtassistant.constant.GlobalVariable;
import cn.xiejx.ddtassistant.exception.FrontException;
import cn.xiejx.ddtassistant.utils.SpringContextUtil;
import cn.xiejx.ddtassistant.utils.Util;
import cn.xiejx.ddtassistant.vo.MyString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

    public List<List<String>> getAvailableIpAddr() {
        List<List<String>> res = new ArrayList<>();
        List<String> lanIpv4 = new ArrayList<>();
        List<String> wanIpv4 = new ArrayList<>();
        List<String> lanIpv6 = new ArrayList<>();
        List<String> wanIpv6 = new ArrayList<>();
        res.add(lanIpv4);
        res.add(wanIpv4);
        res.add(lanIpv6);
        res.add(wanIpv6);

        List<List<String>> localIPList = Util.getLocalIPList();
        if (CollectionUtils.isEmpty(localIPList)) {
            return res;
        }

        List<String> allIpv4List = localIPList.get(0);
        List<String> allIpv6List = localIPList.get(1);

        String[] lanIpv4PrefixList = {
                "0.",
                "10.",
                "127.",
                "169.254.",
                "172.16.", "172.17.", "172.18.", "172.19.", "172.20.", "172.21.", "172.22.", "172.23.", "172.24.", "172.25.", "172.26.", "172.27.", "172.28.", "172.29.", "172.30.", "172.31.",
                "192.168."
        };

        String[] lanIpv6PrefixList = {
                "0:",
                ":",
                "f",
        };

        String port = SpringContextUtil.getBean(Environment.class).getProperty("server.port");

        for (String ip : allIpv4List) {
            boolean match = false;
            for (String lanIpv4Prefix : lanIpv4PrefixList) {
                if (ip.startsWith(lanIpv4Prefix)) {
                    match = true;
                    break;
                }
            }
            String ipWithAddr = "http://" + ip + ":" + port;
            if (match) {
                lanIpv4.add(ipWithAddr);
            } else {
                wanIpv4.add(ipWithAddr);
            }
        }

        for (String ip : allIpv6List) {
            boolean match = false;
            for (String lanIpv6Prefix : lanIpv6PrefixList) {
                if (ip.startsWith(lanIpv6Prefix)) {
                    match = true;
                    break;
                }
            }
            String ipWithAddr = "http://[" + ip + "]:" + port;
            if (match) {
                lanIpv6.add(ipWithAddr);
            } else {
                wanIpv6.add(ipWithAddr);
            }
        }

        return res;
    }

}
