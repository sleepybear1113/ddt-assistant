package cn.xiejx.ddtassistant.update.constant;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2023/02/03 10:16
 */
public class UpdateConstants {
    public enum VersionTypeEnum {
        /**
         * 说明
         */
        INNER_VERSION(1, "内部测试版"),
        ALPHA_VERSION(2, "alpha测试版"),
        BETA_VERSION(3, "beta测试版"),
        STABLE_VERSION(4, "正式版"),
        ;
        private final Integer type;
        private final String name;

        VersionTypeEnum(Integer type, String name) {
            this.type = type;
            this.name = name;
        }

        public Integer getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public static String getVersionTypeEnum(Integer type) {
            for (VersionTypeEnum versionTypeEnum : values()) {
                if (versionTypeEnum.getType().equals(type)) {
                    return versionTypeEnum.name;
                }
            }
            return "";
        }

        public static String getVersionTypeEnumByVersion(Integer version) {
            if (version == null) {
                return "";
            }
            return getVersionTypeEnum(version % 100 % 5);
        }
    }

    public enum updateStrategyEnum {
        /**
         * 更新策略
         */
        UPDATE_ALL(0),
        UPDATE_RECOMMEND(0),
        DELETE(-1),
        ;

        private final Integer type;

        updateStrategyEnum(Integer type) {
            this.type = type;
        }

        public Integer getType() {
            return type;
        }
    }

}
