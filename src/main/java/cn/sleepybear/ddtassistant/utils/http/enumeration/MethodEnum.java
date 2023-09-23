package cn.sleepybear.ddtassistant.utils.http.enumeration;

import lombok.Getter;

/**
 * HTTP 方法枚举
 *
 * @author XJX
 * @date 2021/1/30 16:53
 */
@Getter
public enum MethodEnum {
    /**
     * HTTP 的 get 方法
     */
    METHOD_GET("GET"),
    /**
     * HTTP 的 post 方法
     */
    METHOD_POST("POST"),
    /**
     * HTTP 的 delete 方法
     */
    METHOD_DELETE("DELETE"),
    /**
     * HTTP 的 put 方法
     */
    METHOD_PUT("PUT"),
    /**
     * HTTP 的 head 方法
     */
    METHOD_HEAD("HEAD"),
    /**
     * HTTP 的 patch 方法
     */
    METHOD_PATCH("PATCH"),
    /**
     * HTTP 的 options 方法
     */
    METHOD_OPTIONS("OPTIONS"),
    /**
     * HTTP 的 trace 方法
     */
    METHOD_TRACE("TRACE");

    private final String method;

    MethodEnum(String method) {
        this.method = method;
    }

    public static MethodEnum getByMethodName(String methodName) {
        for (MethodEnum methodEnum : MethodEnum.values()) {
            if (methodEnum.getMethod().equals(methodName)) {
                return methodEnum;
            }
        }
        return null;
    }
}
