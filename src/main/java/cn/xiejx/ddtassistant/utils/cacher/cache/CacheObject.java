package cn.xiejx.ddtassistant.utils.cacher.cache;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/05/04 13:49
 */
public class CacheObject<T> implements Serializable {

    private static final long serialVersionUID = -935036406020467353L;

    /**
     * 缓存对象
     */
    private T obj;

    /**
     * 创建时间
     */
    private long createTime;
    /**
     * 获取时间，指的是调用 get 方法的访问时间
     */
    private long accessTime;
    /**
     * 更新时间，指的是修改了值的时间
     */
    private long updateTime;

    /**
     * 过期时间，毫秒
     */
    private Long expireTime;

    /**
     * 过期策略，参见 {@link ExpireWayEnum}
     */
    private ExpireWayEnum expireWayEnum;

    /**
     * 访问次数
     */
    private final AtomicInteger accessCount = new AtomicInteger(0);
    /**
     * 更新次数
     */
    private final AtomicInteger updateCount = new AtomicInteger(0);

    /**
     * 没有过期时间的构造方法
     *
     * @param obj 缓存对象
     */
    public CacheObject(T obj) {
        this(obj, null);
    }

    /**
     * 默认从创建开始计算过期时间的构造方法
     *
     * @param obj        环处对象
     * @param expireTime 过期时间，毫秒
     */
    public CacheObject(T obj, Long expireTime) {
        this(obj, expireTime, ExpireWayEnum.AFTER_CREATE);
    }

    /**
     * 根构造方法
     *
     * @param obj           缓存时间
     * @param expireTime    过期时间，毫秒
     * @param expireWayEnum 过期策略，参见 {@link ExpireWayEnum}
     */
    public CacheObject(T obj, Long expireTime, ExpireWayEnum expireWayEnum) {
        this.obj = obj;
        this.expireTime = expireTime;
        this.expireWayEnum = expireWayEnum;
        initTime(3);
    }

    /**
     * 初始化时间
     *
     * @param t enum
     */
    private void initTime(int t) {
        long now = System.currentTimeMillis();
        switch (t) {
            case 3:
                this.createTime = now;
            case 2:
                this.updateTime = now;
            case 1:
                this.accessTime = now;
        }
    }

    /**
     * 更新次数
     *
     * @param t enum
     */
    private void increaseCount(int t) {
        switch (t) {
            case 2:
                this.accessCount.incrementAndGet();
            case 1:
                this.updateCount.incrementAndGet();
        }
    }

    public boolean isExpire() {
        return isExpire(null, true);
    }

    /**
     * 是否过期
     *
     * @return boolean
     */
    public boolean isExpire(ExpireWayEnum expireWayEnum, boolean keepOldExpireWay) {
        if (this.expireTime == null) {
            return false;
        }
        if (expireWayEnum == null || keepOldExpireWay) {
            expireWayEnum = this.expireWayEnum;
        }
        if (expireWayEnum == null) {
            expireWayEnum = ExpireWayEnum.AFTER_CREATE;
            this.expireWayEnum = ExpireWayEnum.AFTER_CREATE;
        }
        long time = 0;
        switch (expireWayEnum) {
            case AFTER_UPDATE:
                time = this.updateTime;
                break;
            case AFTER_ACCESS:
                time = this.accessTime;
                break;
            case AFTER_CREATE:
                time = this.createTime;
                break;
        }

        return this.expireTime + time <= System.currentTimeMillis();
    }

    /**
     * 过期时间点，若没有过期时间则返回 null
     *
     * @return 毫秒的时间戳
     */
    public Long getExpireAt() {
        if (this.expireTime == null) {
            return null;
        }
        long time = 0;
        switch (this.expireWayEnum) {
            case AFTER_UPDATE:
                time = this.updateTime;
                break;
            case AFTER_ACCESS:
                time = this.accessTime;
                break;
            case AFTER_CREATE:
                time = this.createTime;
                break;
        }

        return this.expireTime + time;
    }

    /* ======================= 以下都是 Getter/Setter 相关方法 ======================= */

    /**
     * 获取缓存对象<br/>
     * 需要加一次访问次数和更新访问时间
     *
     * @return T
     */
    public T getObj() {
        initTime(1);
        increaseCount(1);
        return getObjPure();
    }

    public T getObjPure() {
        return obj;
    }

    public CacheObject<T> getCacheObject() {
        initTime(1);
        increaseCount(1);
        return this;
    }

    /**
     * 更新缓存对象<br/>
     * 需要更新一次访问和更新次数，以及时间
     *
     * @param obj T
     */
    public void setObj(T obj, Long expireTime, ExpireWayEnum expireWayEnum) {
        this.obj = obj;
        if (expireTime != null) {
            this.expireTime = expireTime;
        }
        if (expireWayEnum != null) {
            this.expireWayEnum = expireWayEnum;
        }
        initTime(2);
        increaseCount(2);
    }

    public long getCreateTime() {
        return createTime;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }

    public ExpireWayEnum getExpireWayEnum() {
        return expireWayEnum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CacheObject<?> that = (CacheObject<?>) o;

        return Objects.equals(obj, that.obj);
    }

    @Override
    public int hashCode() {
        return obj != null ? obj.hashCode() : 0;
    }
}
