package cn.xiejx.ddtassistant.vo;

/**
 * @author XJX
 * @date 2021/8/10 0:38
 */
public class ResultCodeConstant {
    public enum CodeEnum {
        /**
         * 正常情况下的返回值
         */
        SUCCESS(0),
        /**
         * 抛出 {@link cn.xiejx.ddtassistant.exception.FrontException} 时候的返回值
         */
        COMMON_ERROR(-1),
        /**
         * 其他错误
         */
        SYSTEM_ERROR(-10),
        ;
        private final Integer code;

        CodeEnum(Integer code) {
            this.code = code;
        }

        public Integer getCode() {
            return code;
        }
    }
}
