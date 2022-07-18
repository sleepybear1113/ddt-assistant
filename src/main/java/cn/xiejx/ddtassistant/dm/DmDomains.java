package cn.xiejx.ddtassistant.dm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * @author sleepybear
 */
public class DmDomains {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PicEx {
        int index;
        int x;
        int y;
        String picName;

        public static boolean contains(List<PicEx> list, String s) {
            if (CollectionUtils.isEmpty(list)) {
                return false;
            }
            for (PicEx picEx : list) {
                if (picEx.picName.contains(s)) {
                    return true;
                }
            }
            return false;
        }

        public static boolean contains(List<PicEx> list, List<String> s) {
            for (String s1 : s) {
                boolean contains = contains(list, s1);
                if (contains) {
                    return true;
                }
            }

            return false;
        }
    }
}
