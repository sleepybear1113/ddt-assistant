package cn.sleepybear.ddtassistant.logic;

import cn.sleepybear.ddtassistant.annotation.ReCaptureRequestMapping;
import cn.sleepybear.ddtassistant.constant.GlobalVariable;
import cn.sleepybear.ddtassistant.controller.ReCaptureController;
import cn.sleepybear.ddtassistant.type.BaseType;
import cn.sleepybear.ddtassistant.type.TypeConstants;
import cn.sleepybear.ddtassistant.type.reCapture.RecaptureDomain;
import cn.sleepybear.ddtassistant.utils.Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author sleepybear
 */
@Component
@Slf4j
public class ReCaptureLogic {

    public Boolean deleteSampleImg(String src) {
        if (StringUtils.isBlank(src)) {
            return false;
        }

        if (!new File(src).exists()) {
            log.info("文件删除失败！【{}】不存在", src);
            return false;
        }
        Util.delayDeleteFile(src, null);
        Util.sleep(100L);
        TypeConstants.TemplatePrefix.initTemplateImgMap();
        return true;
    }

    public List<String> getTemplates(String templatePrefix, boolean includeTemp) {
        if (StringUtils.isBlank(templatePrefix)) {
            return new ArrayList<>();
        }
        return GlobalVariable.getTemplateImgList(templatePrefix, includeTemp);
    }

    public List<RecaptureDomain> getAllTemplateInfoList() {
        List<RecaptureDomain> list = new ArrayList<>();
        Class<?> clazz = ReCaptureController.class;
        Method[] declaredMethods = clazz.getDeclaredMethods();
        for (Method method : declaredMethods) {
            Annotation[] annotations = method.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation instanceof ReCaptureRequestMapping) {
                    list.add(new RecaptureDomain((ReCaptureRequestMapping) annotation));
                }
            }
        }
        list.sort(Comparator.comparing(RecaptureDomain::getPrefix));
        return list;
    }

    // ===============================================================
    // ===============================================================
    // ===============================================================
    // ===============================================================
    // ===============================================================

    public <T extends BaseType> List<String> invokeCapture(Integer[] hwnds, String methodName, String path, Class<T> clazz) {
        List<String> pathList = new ArrayList<>();
        if (hwnds == null || hwnds.length == 0) {
            return pathList;
        }
        for (Integer hwnd : hwnds) {
            T t = BaseType.createInstance(hwnd, clazz, true, null);
            if (!t.getDm().isWindowClassFlashPlayerActiveX()) {
                continue;
            }
            t.getDm().clickCorner();
            Util.sleep(100L);
            try {
                Method method = clazz.getMethod(methodName, String.class);
                method.invoke(t, path);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                log.info("类 {} 反射调用方法【{}】失败，原因：{}", t.getClass().getSimpleName(), methodName, e.getMessage(), e);
                return pathList;
            }
            pathList.add(path);
        }
        if (CollectionUtils.isNotEmpty(pathList)) {
            TypeConstants.TemplatePrefix.initTemplateImgMap();
        }
        return pathList;
    }

    public Boolean convertToOfficialTemplate(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return false;
        }

        String replace = path.replace(TypeConstants.TemplatePrefix.TEMP_STR, "");
        try {
            Files.move(file.toPath(), new File(replace).toPath(), StandardCopyOption.REPLACE_EXISTING);
            TypeConstants.TemplatePrefix.initTemplateImgMap();
            return true;
        } catch (IOException e) {
            log.info("重命名失败：{}", path);
            return false;
        }
    }
}
