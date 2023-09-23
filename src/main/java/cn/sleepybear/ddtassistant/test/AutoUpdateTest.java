package cn.sleepybear.ddtassistant.test;

import lombok.Data;
import org.apache.commons.codec.binary.Hex;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2023/01/14 19:23
 */
public class AutoUpdateTest {
    public static void main(String[] args) throws Exception {
        String basePath = "D:\\111\\ddt-assistant存档\\ddt-assistant-2.2.1.1";
        List<File> allFiles = getAllFiles(basePath);
        for (File file : allFiles) {
            String md5 = calcMd5(file);

            FileInfo fileInfo = new FileInfo(file.getAbsoluteFile().toString().replace(basePath + "\\", ""), md5, file.length());

            System.out.println(fileInfo + ",\n");
        }
    }


    public static List<File> getAllFiles(String filepath) {
        List<File> allFiles = new ArrayList<>();
        findFolder(new File(filepath), allFiles);
        return allFiles;
    }

    private static void findFolder(File file, List<File> allFiles) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null || files.length == 0) {
                return;
            }
            for (File f : files) {
                findFolder(f, allFiles);
            }
        } else {
            allFiles.add(file);
        }
    }

    public static String calcMd5(File file) throws NoSuchAlgorithmException, IOException {
        if (file == null || !file.exists()) {
            return null;
        }
        MessageDigest messageDigestMd5 = MessageDigest.getInstance("MD5");
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] buffer = new byte[8192];
        int length;
        while ((length = fileInputStream.read(buffer)) != -1) {
            messageDigestMd5.update(buffer, 0, length);
        }
        fileInputStream.close();
        return new String(Hex.encodeHex(messageDigestMd5.digest()));
    }

    @Data
    static class FileInfo {
        private String filename;
        private String md5;
        private long size;
        private String path;

        public FileInfo(String filename, String md5, long size) {
            this.filename = filename.replace("\\", "/");
            this.md5 = md5;
            this.size = size;
        }

        @Override
        public String toString() {
            String template = """
                    {
                          "filename": "%s",
                          "md5": "%s",
                          "size": %s,
                          "path": "%s"
                        }""";
            return String.format(template, filename, md5, size, filename);
        }
    }

}
