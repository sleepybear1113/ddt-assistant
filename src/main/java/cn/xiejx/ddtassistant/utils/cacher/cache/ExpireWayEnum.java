package cn.xiejx.ddtassistant.utils.cacher.cache;

/**
 * 过期策略枚举
 *
 * @author sleepybear
 * @date 2022/05/04 19:53
 */
public enum ExpireWayEnum {
    /**
     * 以创建时间为准
     */
    AFTER_CREATE,
    /**
     * 以最后一次访问时间为准
     */
    AFTER_ACCESS,
    /**
     * 以最后一次更新时间为准
     */
    AFTER_UPDATE,
}
