package cn.xiejx.ddtassistant.utils;

import java.io.*;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * @author sleepybear
 */
public class Util {
    public static final int TIME_ALL_FORMAT = 0;
    public static final int TIME_YMD_FORMAT = 1;
    public static final int TIME_HMS_FORMAT = 2;
    public static void sleep(long t) {
        try {
            if (t > 0) {
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
}
