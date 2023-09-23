package cn.sleepybear.ddtassistant.update.helper;

import cn.sleepybear.ddtassistant.base.UpdateConfig;
import cn.sleepybear.ddtassistant.update.constant.UpdateConstants;
import cn.sleepybear.ddtassistant.update.domain.MainVersion;
import cn.sleepybear.ddtassistant.update.domain.UpdateList;
import cn.sleepybear.ddtassistant.update.vo.MainVersionInfoVo;
import cn.sleepybear.ddtassistant.update.vo.UpdateFileInfoVo;
import cn.sleepybear.ddtassistant.update.vo.UpdateInfoVo;
import cn.sleepybear.ddtassistant.update.vo.UpdateListVo;
import cn.sleepybear.ddtassistant.utils.Util;
import cn.sleepybear.ddtassistant.utils.http.HttpHelper;
import cn.sleepybear.ddtassistant.utils.http.HttpResponseHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2023/02/03 09:18
 */
@Slf4j
public class UpdateHelper {

    public static boolean downloadFile(UpdateFileInfoVo updateFileInfoVo) {
        if (updateFileInfoVo == null) {
            return false;
        }

        String url = updateFileInfoVo.getUrl();
        if (StringUtils.isBlank(url)) {
            log.info("远程文件[{}]没有提供下载链接", updateFileInfoVo.getFilename());
            return false;
        }

        if (updateFileInfoVo.getSame()) {
            return false;
        }

        UpdateConstants.TypeEnum typeEnum = UpdateConstants.TypeEnum.getType(updateFileInfoVo.getType());


        String filename = updateFileInfoVo.getPath() + updateFileInfoVo.getFilename();
        String rename;
        File file = new File(filename);
        if (file.exists()) {
            String md5 = Util.calcMd5(file);
            if (updateFileInfoVo.getRemoteMd5().equals(md5)) {
                log.info("本地文件[{}]存在，无须下载", filename);
                return true;
            }
            rename = updateFileInfoVo.getPath() + "-" + System.currentTimeMillis() + "-" + updateFileInfoVo.getFilename();
            file = new File(rename);
        } else {
            rename = filename;
        }
        Util.ensureParentDir(rename);

        log.info("开始下载文件[{}]...", updateFileInfoVo.getFilename());
        long start = System.currentTimeMillis();
        HttpHelper httpHelper = HttpHelper.makeDefaultGetHttpHelper(url);
        HttpResponseHelper responseHelper = httpHelper.request();
        long end = System.currentTimeMillis();

        if (UpdateConstants.TypeEnum.BINARY.equals(typeEnum)) {
            byte[] responseBodyBytes = responseHelper.getResponseBodyBytes();
            if (responseBodyBytes == null || responseBodyBytes.length == 0) {
                log.warn("文件[{}]下载失败, 耗时{}ms", updateFileInfoVo.getFilename(), (end - start));
                return false;
            } else {
                log.info("文件[{}]下载成功, 耗时{}ms, 写入磁盘中...", updateFileInfoVo.getFilename(), (end - start));
            }

            try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                fileOutputStream.write(responseBodyBytes);
                fileOutputStream.flush();
            } catch (IOException e) {
                log.warn(e.getMessage(), e);
                return false;
            }
        } else {
            String responseBody = responseHelper.getResponseBody();
            if (StringUtils.isBlank(responseBody)) {
                log.warn("文件[{}]下载失败, 耗时{}ms", updateFileInfoVo.getFilename(), (end - start));
                return false;
            }
            responseBody = responseBody.replace("\r\n", "\n").replace("\n", "\r\n");
            Util.writeFile(responseBody, rename);
            log.info("文件[{}]下载成功, 耗时{}ms, 写入磁盘中...", updateFileInfoVo.getFilename(), (end - start));
        }

        String md5 = Util.calcMd5(file);
        if (updateFileInfoVo.getRemoteMd5().equals(md5)) {
            log.info("文件[{}]校验成功", updateFileInfoVo.getFilename());
            if (!filename.equals(rename)) {
                File oldFile = new File(filename);
                if (!oldFile.delete()) {
                    log.warn("旧同名文件[{}]删除失败，重命名文件失败", filename);
                    return false;
                } else {
                    if (file.renameTo(oldFile)) {
                        log.info("文件[{}]写入完成", updateFileInfoVo.getFilename());
                        return true;
                    } else {
                        log.warn("文件[{}]重命名失败", filename);
                        return false;
                    }
                }
            } else {
                log.info("文件[{}]写入完成", updateFileInfoVo.getFilename());
                return true;
            }
        } else {
            log.warn("文件[{}]校验失败，删除已下载的文件", updateFileInfoVo.getFilename());
            Util.delayDeleteFile(file, 0L);
            return false;
        }
    }

    /**
     * 获取更新的主方法
     *
     * @param currentVersion 当前版本
     * @param updateConfig   更新设置
     * @return 更新 vo
     */
    public static UpdateInfoVo checkUpdate(int currentVersion, UpdateConfig updateConfig) {
        UpdateInfoVo updateInfoVo;
        if (updateConfig.getUpdateVersionType() == null) {
            updateInfoVo = new UpdateInfoVo();
            updateInfoVo.setMessage("未设置更新获取版本");
            return updateInfoVo;
        }
        MainVersion mainVersion = getMainVersion(updateConfig.getUrl());
        updateInfoVo = UpdateInfoVo.build(mainVersion, currentVersion);
        List<MainVersionInfoVo> versionInfoList = updateInfoVo.getVersionInfoList();

        if (CollectionUtils.isEmpty(versionInfoList)) {
            return updateInfoVo;
        }

        // 过滤旧版本
        versionInfoList.removeIf(v -> (v.getVersionType() & updateConfig.getUpdateVersionType()) <= 0);

        StringBuilder sb = new StringBuilder();
        for (MainVersionInfoVo mainVersionInfoVo : versionInfoList) {
            String updateMainFilePath = mainVersionInfoVo.getUpdateMainFilePath();
            UpdateList updateList = getUpdateList(updateMainFilePath);
            UpdateListVo updateListVo = UpdateListVo.build(updateList);
            mainVersionInfoVo.setUpdateListVo(updateListVo);
            sb.append(mainVersionInfoVo.updateInfo());
        }
        String info = sb.toString();
        if (StringUtils.isNotBlank(info)) {
            log.info(info);
        }
        // 过滤没有更新文件的
        versionInfoList.removeIf(v -> v.getUpdateListVo() == null || CollectionUtils.isEmpty(v.getUpdateListVo().getStatics()));

        log.info("新版本检测完毕");

        return updateInfoVo;
    }

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
        String path = "D:\\XJXCode\\Raw\\ddt-assistant-static\\versions\\2.4.1\\files\\";
        List<File> files = Util.listFiles(path);

        List<UpdateFileInfoVo> list = new ArrayList<>();
        for (File file : files) {
            list.add(UpdateFileInfoVo.buildRemote(file, path));
        }
        for (UpdateFileInfoVo updateFileInfoVo : list) {
            String filename = updateFileInfoVo.getFilename();
            if (filename.contains("ddt-assistant")) {
                updateFileInfoVo.setUrl("");
                updateFileInfoVo.setInfo("请移步群文件下载");
            }
        }

        System.out.println(Util.parseObjectToJsonString(list).replace("\\\\", "/"));
    }

    public static void main(String[] args) throws IOException {
        calcAllMd5();
    }

    public static void test() throws IOException {
        //        calcAllMd5();
        String path = "D:\\XJXCode\\Java\\Spring\\ddt-assistant\\大漠插件注册\\取消注册大漠.bat";
        System.out.println(Util.calcMd5(new File(path)));
        List<String> strings = Files.readAllLines(Paths.get(path));
        System.out.println(strings.get(strings.size() - 1).endsWith("\r\n"));
        System.out.println(strings.get(0).endsWith("\n"));
    }

}
