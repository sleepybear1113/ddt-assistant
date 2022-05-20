package cn.xiejx.ddtassistant.config;

import cn.xiejx.ddtassistant.constant.Constants;
import cn.xiejx.ddtassistant.dm.Dm;
import cn.xiejx.ddtassistant.dm.DmDdt;
import cn.xiejx.ddtassistant.type.Captcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * @author sleepybear
 */
@Configuration
@Slf4j
public class Ioc {

    @Bean
    public UserConfig init() {
        return UserConfig.readFromFile();
    }

    @Bean
    public DmDdt initDmDdt() {
        DmDdt dmDdt = DmDdt.createInstance(null);
        captchaImgToBmpBatch(dmDdt, Constants.RESOURCE_DIR);
        return dmDdt;
    }

    public static void captchaImgToBmpBatch(Dm dm, String path) {
        File imgDir = new File(path);
        if (!imgDir.exists()) {
            return;
        }
        File[] files = imgDir.listFiles();
        if (files == null || files.length == 0) {
            return;
        }
        Set<String> bmpFileNameSet = new HashSet<>();
        Set<String> otherFileNameSet = new HashSet<>();
        for (File file : files) {
            if (!file.isFile()) {
                continue;
            }
            String fileName = file.getName();
            if (!fileName.startsWith(Captcha.TEMPLATE_PIC_PREFIX)) {
                // 需要以 template- 前缀
                continue;
            }
            if (fileName.endsWith(".bmp")) {
                bmpFileNameSet.add(fileName);
            } else {
                otherFileNameSet.add(fileName);
            }
        }

        for (String otherFileName : otherFileNameSet) {
            if (otherFileName.endsWith(Constants.PNG_SUFFIX) || otherFileName.endsWith(Constants.JPG_SUFFIX)) {
                String newBmpFileName = otherFileName.substring(0, otherFileName.length() - 4) + Constants.BMP_SUFFIX;
                if (bmpFileNameSet.contains(newBmpFileName)) {
                    continue;
                }
                dm.imageToBmp(path + otherFileName, path + newBmpFileName);
                bmpFileNameSet.add(newBmpFileName);
                log.info("图片 {} 转换为 {}", otherFileName, newBmpFileName);
            }
        }

        for (String fileName : bmpFileNameSet) {
            if (fileName.contains("dark")) {
                Captcha.captchaTemplateNameDarkList.add(path + fileName);
            } else {
                Captcha.captchaTemplateNameList.add(path + fileName);
            }
        }
    }
}
