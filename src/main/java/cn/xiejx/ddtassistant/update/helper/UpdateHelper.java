package cn.xiejx.ddtassistant.update.helper;

import cn.xiejx.ddtassistant.update.constant.UpdateConstants;
import cn.xiejx.ddtassistant.update.domain.MainVersion;
import cn.xiejx.ddtassistant.update.domain.UpdateList;
import cn.xiejx.ddtassistant.update.vo.FileInfoVo;
import cn.xiejx.ddtassistant.update.vo.MainVersionInfoVo;
import cn.xiejx.ddtassistant.update.vo.UpdateInfoVo;
import cn.xiejx.ddtassistant.update.vo.UpdateListVo;
import cn.xiejx.ddtassistant.utils.Util;
import cn.xiejx.ddtassistant.utils.http.HttpHelper;
import cn.xiejx.ddtassistant.utils.http.HttpResponseHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2023/02/03 09:18
 */
public class UpdateHelper {

    public static MainVersion getMainVersion(String url) {
        HttpHelper httpHelper = HttpHelper.makeDefaultGetHttpHelper(url);
        HttpResponseHelper responseHelper = httpHelper.request();
        if (responseHelper.getStatusLine().getStatusCode() != 200) {
            return null;
        }
        String responseBody = responseHelper.getResponseBody();
        if (StringUtils.isBlank(responseBody)) {
            return null;
        }

        return Util.parseJsonToObject(responseBody, MainVersion.class);
    }

    public static UpdateInfoVo checkUpdate(int currentVersion, String url) {
        MainVersion mainVersion = getMainVersion(url);
        UpdateInfoVo updateInfoVo = UpdateInfoVo.build(mainVersion, currentVersion);
        List<MainVersionInfoVo> versionInfoList = updateInfoVo.getVersionInfoList();

        if (CollectionUtils.isEmpty(versionInfoList)) {
            return updateInfoVo;
        }

        for (MainVersionInfoVo mainVersionInfoVo : versionInfoList) {
            String updateMainFilePath = mainVersionInfoVo.getUpdateMainFilePath();
            UpdateList updateList = getUpdateList(updateMainFilePath);
            UpdateListVo updateListVo = UpdateListVo.build(updateList);
            mainVersionInfoVo.setUpdateListVo(updateListVo);
        }

        return updateInfoVo;
    }

    public static UpdateList getUpdateList(String url) {
        HttpHelper httpHelper = HttpHelper.makeDefaultGetHttpHelper(url);
        HttpResponseHelper responseHelper = httpHelper.request();
        if (!responseHelper.isResponse2xx()) {
            return null;
        }
        String responseBody = responseHelper.getResponseBody();
        if (StringUtils.isBlank(responseBody)) {
            return null;
        }
        return Util.parseJsonToObject(responseBody, UpdateList.class);
    }

    public static void calcAllMd5() {
        String path = "D:\\XJXCode\\Raw\\ddt-assistant-static\\versions\\2.3.3\\files\\";
        List<File> files = Util.listFiles(path);

        List<FileInfoVo> list = new ArrayList<>();
        for (File file : files) {
            list.add(FileInfoVo.buildRemote(file, path));
        }

        System.out.println(Util.parseObjectToJsonString(list).replace("\\\\", "/"));
    }

    public static void main(String[] args) {
//        calcAllMd5();
        UpdateInfoVo updateInfoVo = checkUpdate(1000, "https://gitee.com/sleepybear1113/ddt-assistant-static/raw/test/version.json");
        System.out.println(updateInfoVo);
    }
}
