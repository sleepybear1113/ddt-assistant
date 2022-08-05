package cn.xiejx.ddtassistant.utils;

import cn.xiejx.ddtassistant.constant.GlobalVariable;
import cn.xiejx.ddtassistant.utils.http.HttpHelper;
import cn.xiejx.ddtassistant.utils.http.HttpRequestMaker;
import cn.xiejx.ddtassistant.utils.tj.ChoiceEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
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

    public static <T> T parseJsonToObject(String s, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return parseJsonToObject(s, clazz, mapper);
    }

    public static <T> T parseJsonToObject(String s, Class<T> clazz, ObjectMapper mapper) {
        try {
            return mapper.readValue(s, clazz);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return null;
        }
    }

    public static <T> T readFile(String path, Class<T> clazz) {
        try {
            String s = readFile(path);
            return parseJsonToObject(s, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    public static String readFile(String path) {
        StringBuilder s = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(path)), StandardCharsets.UTF_8))) {
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                s.append(line).append(System.getProperty("line.separator"));
            }
            return s.toString();
        } catch (IOException e) {
            return null;
        }
    }

    public static <T> void writeFile(Object obj, String path) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
            writeFile(result, path);
        } catch (JsonProcessingException ignored) {
        }
    }

    public static void writeFile(String s, String path) {
        if (StringUtils.isBlank(path)) {
            return;
        }
        ensureParentDir(path);
        try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get(path)), StandardCharsets.UTF_8))) {
//            bufferedWriter.write(s.replace("\\n", ""));
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

    public static void ensureParentDir(String filename) {
        File file = new File(filename);
        File parentFile = file.getParentFile();
        if (parentFile == null) {
            return;
        }
        if (!parentFile.exists()) {
            if (!parentFile.mkdirs()) {
                log.info("创建文件夹 {} 失败", parentFile);
            }
        }
    }

    public static void delayDeleteFile(String path, Long delay) {
        if (delay == null || delay <= 0) {
            if (!new File(path).delete()) {
                log.info("删除文件失败 {} 失败", path);
            }
            return;
        }
        GlobalVariable.THREAD_POOL.execute(() -> {
            sleep(delay);
            if (!new File(path).delete()) {
                log.info("删除文件失败 {} 失败", path);
            }
        });
    }

    public static byte[] fileToBytes(String path) {
        File file = new File(path);
        byte[] bytes;
        try (FileInputStream inputStream = new FileInputStream(file)) {
            bytes = new byte[inputStream.available()];
            int read = inputStream.read(bytes, 0, inputStream.available());
        } catch (IOException e) {
            return null;
        }

        Util.delayDeleteFile(path, 3000L);
        return bytes;
    }

    public static boolean isNumber(String s) {
        try {
            new BigDecimal(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static Integer getIntegerNumber(String s) {
        if (!isNumber(s)) {
            return null;
        }
        return new BigDecimal(s).intValue();
    }

    public static String getIntegerNumberWithSign(String s) {
        if (!isNumber(s)) {
            return null;
        }
        char c = s.charAt(0);
        String s1 = String.valueOf(new BigDecimal(s).intValue());
        if (c == '+') {
            return c + s1;
        }
        return s1;
    }

    public static <T> List<String> getAllDeclaredFields(Class<T> clazz) {
        List<String> list = new ArrayList<>();
        try {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                list.add(field.get(field.getName()).toString());
            }
            return list;
        } catch (IllegalAccessException e) {
            log.warn(e.getMessage(), e);
            return list;
        }
    }

    public static void openWithExplorer(String path, boolean select) {
        File file = new File(path);
        boolean b = file.exists();
        String e1 = "explorer.exe ";
        String e2 = "explorer.exe /select, ";
        if (b) {
            String cmd = select ? e2 : e1;
            try {
                Runtime.getRuntime().exec(cmd + file.getAbsolutePath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static boolean testAdmin() {
        File file = new File("C:\\测试管理员空文件-1113.txt");
        if (file.exists()) {
            return file.delete();
        } else {
            try {
                boolean newFile = file.createNewFile();
                boolean delete = file.delete();
                return newFile && delete;
            } catch (IOException e) {
                return false;
            }
        }
    }

    public static void main(String[] args) {
        openWithExplorer("资源图片/模板/副本-验证码-倒计时-1.bmp", true);
    }
}
