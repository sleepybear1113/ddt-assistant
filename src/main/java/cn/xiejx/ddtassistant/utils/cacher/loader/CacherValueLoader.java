package cn.xiejx.ddtassistant.utils.cacher.loader;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/05/07 11:25
 */
@FunctionalInterface
public interface CacherValueLoader<K, V> {

    /**
     * 加载缓存
     *
     * @param key key
     * @return value
     */
    V load(K key);
}
