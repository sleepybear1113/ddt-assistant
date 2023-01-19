package cn.xiejx.ddtassistant.dm;

/**
 * @author sleepybear
 */
public class DmConstants {

    /**
     * 找图的方式，查找方向
     */
    public enum SearchWay {
        /**
         * 从左到右，从上到下
         */
        LEFT2RIGHT_UP2DOWN(0),
        /**
         * 从左到右，从下到上
         */
        LEFT2RIGHT_DOWN2UP(1),
        /**
         * 从右到左，从上到下
         */
        RIGHT2LEFT_UP2DOWN(2),
        /**
         * 从右到左，从下到上
         */
        RIGHT2LEFT_DOWN2UP(3),
        ;
        private final Integer type;

        SearchWay(int type) {
            this.type = type;
        }

        public Integer getType() {
            return type;
        }
    }

    /**
     * 这些值可以相加，比如 4+8+16 就是类似于任务管理器中的窗口列表
     */
    public enum EnumWindowFilter {
        /**
         * 匹配窗口标题，参数 title 有效
         */
        TITLE(1),
        /**
         * 匹配窗口类名，参数 class_name 有效
         */
        CLASS_NAME(2),
        /**
         * 只匹配指定父窗口的第一层孩子窗口
         */
        FIRST_CHILD(4),
        /**
         * 匹配父窗口为 0 的窗口，即顶级窗口
         */
        TOP_WINDOW(8),
        /**
         * 匹配可见窗口
         */
        VISIBLE(16),
        /**
         * 匹配出的窗口按照窗口打开顺序依次排列
         */
        ORDER_BY_OPEN(32),
        ;
        private final Integer type;

        EnumWindowFilter(int type) {
            this.type = type;
        }

        public Integer getType() {
            return type;
        }
    }

}
