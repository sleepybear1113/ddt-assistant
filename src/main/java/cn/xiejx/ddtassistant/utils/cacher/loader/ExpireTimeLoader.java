package cn.xiejx.ddtassistant.utils.cacher.loader;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/05/07 21:49
 */
@FunctionalInterface
public interface ExpireTimeLoader<K> {

    /**
     * 获取缓存的过期时间
     *
     * @param key key
     * @return 毫秒
     */
    Long getLoadExpireTime(K key);
}
