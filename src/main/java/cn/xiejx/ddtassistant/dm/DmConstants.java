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
}
