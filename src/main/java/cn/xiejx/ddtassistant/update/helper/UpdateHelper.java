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

    public static boolean downloadFile(FileInfoVo fileInfoVo) {
        if (fileInfoVo == null) {
            return false;
        }

        String url = fileInfoVo.getUrl();
        if (StringUtils.isBlank(url)) {
            return false;
        }

        if (fileInfoVo.getSame()) {
            return false;
        }

        UpdateConstants.TypeEnum typeEnum = UpdateConstants.TypeEnum.getType(fileInfoVo.getType());


        String filename = fileInfoVo.getPath() + fileInfoVo.getFilename();
        String rename;
        File file = new File(filename);
        if (file.exists()) {
            String md5 = Util.calcMd5(file);
            if (fileInfoVo.getRemoteMd5().equals(md5)) {
                log.info("本地文件[{}]存在，无须下载", filename);
                return true;
            }
            rename = fileInfoVo.getPath() + "-" + System.currentTimeMillis() + "-" + fileInfoVo.getFilename();
            file = new File(rename);
        } else {
            rename = filename;
        }
        Util.ensureParentDir(rename);

        log.info("开始下载文件[{}]...", fileInfoVo.getFilename());
        long start = System.currentTimeMillis();
        HttpHelper httpHelper = HttpHelper.makeDefaultGetHttpHelper(url);
        HttpResponseHelper responseHelper = httpHelper.request();
        long end = System.currentTimeMillis();

        if (UpdateConstants.TypeEnum.BINARY.equals(typeEnum)) {
            byte[] responseBodyBytes = responseHelper.getResponseBodyBytes();
            if (responseBodyBytes == null || responseBodyBytes.length == 0) {
                log.warn("文件[{}]下载失败, 耗时{}ms", fileInfoVo.getFilename(), (end - start));
                return false;
            } else {
                log.info("文件[{}]下载成功, 耗时{}ms, 写入磁盘中...", fileInfoVo.getFilename(), (end - start));
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
                log.warn("文件[{}]下载失败, 耗时{}ms", fileInfoVo.getFilename(), (end - start));
                return false;
            }
            responseBody = responseBody.replace("\r\n", "\n").replace("\n", "\r\n");
            Util.writeFile(responseBody, rename);
            log.info("文件[{}]下载成功, 耗时{}ms, 写入磁盘中...", fileInfoVo.getFilename(), (end - start));
        }

        String md5 = Util.calcMd5(file);
        if (fileInfoVo.getRemoteMd5().equals(md5)) {
            log.info("文件[{}]校验成功", fileInfoVo.getFilename());
            if (!filename.equals(rename)) {
                File oldFile = new File(filename);
                if (!oldFile.delete()) {
                    log.warn("旧同名文件[{}]删除失败，重命名文件失败", filename);
                    return false;
                } else {
                    if (file.renameTo(oldFile)) {
                        log.info("文件[{}]写入完成", fileInfoVo.getFilename());
                        return true;
                    } else {
                        log.warn("文件[{}]重命名失败", filename);
                        return false;
                    }
                }
            } else {
                log.info("文件[{}]写入完成", fileInfoVo.getFilename());
                return true;
            }
        } else {
            log.warn("文件[{}]校验失败", fileInfoVo.getFilename());
            return false;
        }
    }

    public static void downloadFile(String url, String filename) {
        HttpHelper httpHelper = HttpHelper.makeDefaultGetHttpHelper(url);
        HttpResponseHelper responseHelper = httpHelper.request();
        long end = System.currentTimeMillis();
        byte[] responseBodyBytes = responseHelper.getResponseBodyBytes();
        if (responseBodyBytes == null || responseBodyBytes.length == 0) {
            return;
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(filename)) {
            fileOutputStream.write(responseBodyBytes);
            fileOutputStream.flush();
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
        }
    }

    /**
     * 获取更新的主方法
     *
     * @param currentVersion 当前版本
     * @param url            更新链接
     * @return 更新 vo
     */
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
        String path = "D:\\XJXCode\\Raw\\ddt-assistant-static\\versions\\2.3.3\\files\\";
        List<File> files = Util.listFiles(path);

        List<FileInfoVo> list = new ArrayList<>();
        for (File file : files) {
            list.add(FileInfoVo.buildRemote(file, path));
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

    public static void down() {
        String url = "https://gitee.com/sleepybear1113/ddt-assistant-static/raw/test/versions/2.3.3/files/%E5%A4%A7%E6%BC%A0%E6%8F%92%E4%BB%B6%E6%B3%A8%E5%86%8C/dm.dll";
        String filename = "tmp/test.xxz";
        downloadFile(url, filename);
        File file = new File(filename);
        System.out.println(file.length());
        System.out.println(Util.calcMd5(file));

        String pp = "D:\\XJXCode\\Raw\\ddt-assistant-static\\versions\\2.3.3\\files\\大漠插件注册\\dm.dll";
        File file1 = new File(pp);
        System.out.println(file1.length());
        System.out.println(Util.calcMd5(file1));
    }
}
