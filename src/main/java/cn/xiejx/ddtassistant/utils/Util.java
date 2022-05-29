package cn.xiejx.ddtassistant.utils;

import cn.xiejx.ddtassistant.utils.http.HttpHelper;
import cn.xiejx.ddtassistant.utils.http.HttpRequestMaker;
import cn.xiejx.ddtassistant.utils.http.HttpResponseHelper;
import cn.xiejx.ddtassistant.utils.http.enumeration.MethodEnum;
import cn.xiejx.ddtassistant.utils.tj.ChoiceEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.io.*;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * @author sleepybear
 */
@Slf4j
public class Util {
    public static final int TIME_ALL_FORMAT = 0;
    public static final int TIME_YMD_FORMAT = 1;
    public static final int TIME_HMS_FORMAT = 2;

    public static final String SERVER_HOST = "http://sleepybear1113.com/ddt2";
    public static final String SERVER_UPLOAD_FILE_URL = SERVER_HOST + "/file/upload?fileName=%s&answer=%s&sign=%s";
    public static final String SERVER_DELETE_FILE_URL = SERVER_HOST + "/file/delete?fileName=%s";

    public static final String SERVER_FUNC_PARAM_COLLECTOR_URL = SERVER_HOST + "/collect/func";

    public static void sleep(Long t) {
        try {
            if (t != null && t > 0) {
                TimeUnit.MILLISECONDS.sleep(t);
            }
        } catch (InterruptedException ignored) {
        }
    }

    public static String getTimeString(int type) {
        Calendar instance = Calendar.getInstance(Locale.CHINA);
        int year = instance.get(Calendar.YEAR);
        int month = instance.get(Calendar.MONTH) + 1;
        int day = instance.get(Calendar.DAY_OF_MONTH);
        int hour = instance.get(Calendar.HOUR_OF_DAY);
        int minute = instance.get(Calendar.MINUTE);
        int second = instance.get(Calendar.SECOND);

        if (type == TIME_ALL_FORMAT) {
            return String.format("%d_%02d_%02d_%02d_%02d_%02d", year, month, day, hour, minute, second);
        } else if (type == TIME_YMD_FORMAT) {
            return String.format("%d_%02d_%02d", year, month, day);
        } else if (type == TIME_HMS_FORMAT) {
            return String.format("%02d_%02d_%02d", hour, minute, second);
        }
        return String.format("%d_%02d_%02d_%02d_%02d_%02d", year, month, day, hour, minute, second);
    }

    public static String readFile(String path) {
        StringBuilder s = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path))) {
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                s.append(line);
            }
            return s.toString();
        } catch (IOException e) {
            return null;
        }
    }

    public static void writeFile(String s, String path) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path))) {
            bufferedWriter.write(s);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean portAvailable(int port) {
        try {
            new ServerSocket(port).close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static void uploadToServer(String path, ChoiceEnum answer) {
        if (StringUtils.isBlank(path)) {
            return;
        }
        if (answer == null || ChoiceEnum.UNDEFINED.equals(answer)) {
            return;
        }
        File file = new File(path);
        if (!file.exists()) {
            return;
        }

        try {
            String fileName = file.getName();
            String url = String.format(SERVER_UPLOAD_FILE_URL, fileName, answer.getChoice(), generateFileSign(fileName, answer.getChoice()));
            HttpHelper httpHelper = HttpHelper.makeDefaultTimeoutHttpHelper(HttpRequestMaker.makePostHttpHelper(url));
            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
            multipartEntityBuilder.addBinaryBody("file", file);
            httpHelper.setPostBody(multipartEntityBuilder.build());
            httpHelper.request();
        } catch (Exception e) {
            log.warn("上传文件失败：" + e.getMessage());
        }
    }

    public static void uploadRemoteFuncToServer(String params, String ret) {
        try {

            HttpHelper httpHelper = HttpHelper.makeDefaultTimeoutHttpHelper(SERVER_FUNC_PARAM_COLLECTOR_URL, MethodEnum.METHOD_POST);
            ArrayList<NameValuePair> pairs = new ArrayList<>();
            pairs.add(new BasicNameValuePair("params", params));
            pairs.add(new BasicNameValuePair("ret", ret));
            httpHelper.setUrlEncodedFormPostBody(pairs);
            httpHelper.request();
        } catch (Exception e) {
            log.warn("上传远程调用失败：" + e.getMessage(), e);
        }
    }

    public static void deleteFileFromServer(String path) {
        if (StringUtils.isBlank(path)) {
            return;
        }
        File file = new File(path);
        if (!file.exists()) {
            return;
        }

        try {
            String fileName = file.getName();
            String url = String.format(SERVER_DELETE_FILE_URL, fileName);
            HttpHelper httpHelp = HttpHelper.makeDefaultGetHttpHelper(url);
            httpHelp.request();
        } catch (Exception ignored) {
        }
    }

    private static String generateFileSign(String fileName, String answer) {
        String key = "1113";
        String data = fileName + answer + key;
        return DigestUtils.md5Hex(data);
    }
}
