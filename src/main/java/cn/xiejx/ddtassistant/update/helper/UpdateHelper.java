package cn.xiejx.ddtassistant.update.helper;

import cn.xiejx.ddtassistant.update.domain.MainVersion;
import cn.xiejx.ddtassistant.update.domain.UpdateList;
import cn.xiejx.ddtassistant.update.vo.UpdateInfoVo;
import cn.xiejx.ddtassistant.update.vo.UpdateListVo;
import cn.xiejx.ddtassistant.utils.Util;
import cn.xiejx.ddtassistant.utils.http.HttpHelper;
import cn.xiejx.ddtassistant.utils.http.HttpResponseHelper;
import org.apache.commons.lang3.StringUtils;

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

    public static void main(String[] args) {
        UpdateList updateList = getUpdateList("http://yoga:19876/D%3A/XJXCode/Raw/ddt-assistant-static/versions/2.3.3/main.json");
        UpdateListVo updateListVo = UpdateListVo.build(updateList);
        System.out.println(updateListVo);

    }
}
