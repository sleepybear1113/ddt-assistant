package cn.xiejx.ddtassistant.base;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2023/03/05 23:20
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class VipCoinConfig extends BaseConfig {
    private static final long serialVersionUID = 7600007504607407716L;

    private List<VipCoinOneConfig> vipCoinOneConfigList;

    @Override
    public String getFileName() {
        return "VIP币配置文件.json";
    }

    @Override
    public VipCoinConfig defaultConfig() {
        VipCoinConfig vipCoinConfig = new VipCoinConfig();

        List<VipCoinOneConfig> vipCoinOneConfigList = new ArrayList<>();
        vipCoinConfig.setVipCoinOneConfigList(vipCoinOneConfigList);

        VipCoinOneConfig vipCoinOneConfig = VipCoinOneConfig.defaultConfig();
        vipCoinOneConfigList.add(vipCoinOneConfig);
        return vipCoinConfig;
    }

    /**
     * 单个配置，里面包含条件列表
     */
    @Data
    public static class VipCoinOneConfig implements Serializable {
        private static final long serialVersionUID = 915694950137187709L;

        /**
         * 配置名称
         */
        private String name;
        private String newName;
        private String description;

        private Integer minScore;
        private Boolean autoOpen;
        private Boolean cycleOpen;

        private List<VipCoinCondition> vipCoinConditionList;
        private List<VipCoinThingScore> vipCoinThingScoreList;
        private List<VipCoinStopCondition> vipCoinStopConditionList;

        public static VipCoinOneConfig defaultConfig() {
            return defaultConfig(null);
        }

        public static VipCoinOneConfig defaultConfig(String configName) {
            if (StringUtils.isBlank(configName)) {
                configName = "默认配置";
            }
            VipCoinOneConfig vipCoinOneConfig = new VipCoinOneConfig();
            vipCoinOneConfig.setName(configName);
            String description = "【默认配置】选择了常用的物品作为有价值的，并且将分数设置为 10。\n" +
                    "满足以下任一条件：\n" +
                    "① 盘子分数总和不少于 1 【同时】 <包含> <“极” 字样的物品> 数量 <大于等于> <2> 【同时】 <包含> <任意物品> 数量 <大于等于> <12>。（人话：极武器不少于2个并且有价值不少于12个）\n" +
                    "② 盘子分数总和不少于 1 【同时】 <包含> <“极” 字样的物品> 数量 <大于等于> <3> 【同时】 <包含> <任意物品> 数量 <大于等于> <10> 【同时】 <不包含> <“啵咕盾牌” 字样的物品> 数量类型为 <存在>。（人话：极武器不少于3个并且有价值不少于10个并且不存在包含“远古竹枪”的物品）";
            vipCoinOneConfig.setDescription(description);
            vipCoinOneConfig.setMinScore(1);
            vipCoinOneConfig.setVipCoinThingScoreList(VipCoinThingScore.defaultConfig());

            vipCoinOneConfig.setVipCoinConditionList(VipCoinCondition.defaultConfigList());

            vipCoinOneConfig.setVipCoinStopConditionList(VipCoinStopCondition.defaultConfig());
            return vipCoinOneConfig;
        }
    }

    @Data
    @NoArgsConstructor
    public static class VipCoinThingScore implements Serializable {
        private static final long serialVersionUID = -8073907874896809580L;

        private String name;
        private Integer score;
        private Boolean enable;

        public VipCoinThingScore(String name, Integer score, Boolean enable) {
            this.name = name;
            this.score = score;
            this.enable = enable;
        }

        public boolean matchName(String outName) {
            if (StringUtils.isBlank(outName)) {
                return true;
            }

            for (String s : outName.split("")) {
                if (name.contains(s)) {
                    return false;
                }
            }
            return true;
        }

        public static List<VipCoinThingScore> defaultConfig() {
            List<VipCoinThingScore> vipCoinThingScoreList = new ArrayList<>();
            vipCoinThingScoreList.add(new VipCoinThingScore("极武器-极·医用工具箱", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("极武器-极·司马砸缸", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("极武器-极·烈火", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("极武器-极·牛顿水果篮", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("极武器-极·畅通利器", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("极武器-极·神风", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("极武器-极·轰天", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("极武器-极·雷霆", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("极武器-极·黑白家电", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("真武器-真·医用工具箱", 0, false));
            vipCoinThingScoreList.add(new VipCoinThingScore("真武器-真·司马砸缸", 0, false));
            vipCoinThingScoreList.add(new VipCoinThingScore("真武器-真·烈火", 0, false));
            vipCoinThingScoreList.add(new VipCoinThingScore("真武器-真·牛顿水果篮", 0, false));
            vipCoinThingScoreList.add(new VipCoinThingScore("真武器-真·畅通利器", 0, false));
            vipCoinThingScoreList.add(new VipCoinThingScore("真武器-真·神风", 0, false));
            vipCoinThingScoreList.add(new VipCoinThingScore("真武器-真·轰天", 0, false));
            vipCoinThingScoreList.add(new VipCoinThingScore("真武器-真·雷霆", 0, false));
            vipCoinThingScoreList.add(new VipCoinThingScore("真武器-真·黑白家电", 0, false));
            vipCoinThingScoreList.add(new VipCoinThingScore("其他武器-啵咕盾牌", 0, false));
            vipCoinThingScoreList.add(new VipCoinThingScore("其他武器-天使之赐", 0, false));
            vipCoinThingScoreList.add(new VipCoinThingScore("其他武器-巴罗夫盾牌", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("其他武器-真·天使之赐", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("其他武器-真·爱心回力标", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("其他武器-真·远古竹枪", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("合成石-朱雀石1级", 0, false));
            vipCoinThingScoreList.add(new VipCoinThingScore("合成石-朱雀石2级", 0, false));
            vipCoinThingScoreList.add(new VipCoinThingScore("合成石-朱雀石3级", 0, false));
            vipCoinThingScoreList.add(new VipCoinThingScore("合成石-朱雀石4级", 0, false));
            vipCoinThingScoreList.add(new VipCoinThingScore("合成石-玄武石1级", 0, false));
            vipCoinThingScoreList.add(new VipCoinThingScore("合成石-玄武石2级", 0, false));
            vipCoinThingScoreList.add(new VipCoinThingScore("合成石-玄武石3级", 0, false));
            vipCoinThingScoreList.add(new VipCoinThingScore("合成石-玄武石4级", 0, false));
            vipCoinThingScoreList.add(new VipCoinThingScore("合成石-白虎石1级", 0, false));
            vipCoinThingScoreList.add(new VipCoinThingScore("合成石-白虎石2级", 0, false));
            vipCoinThingScoreList.add(new VipCoinThingScore("合成石-白虎石3级", 0, false));
            vipCoinThingScoreList.add(new VipCoinThingScore("合成石-白虎石4级", 0, false));
            vipCoinThingScoreList.add(new VipCoinThingScore("合成石-青龙石1级", 0, false));
            vipCoinThingScoreList.add(new VipCoinThingScore("合成石-青龙石2级", 0, false));
            vipCoinThingScoreList.add(new VipCoinThingScore("合成石-青龙石3级", 0, false));
            vipCoinThingScoreList.add(new VipCoinThingScore("合成石-青龙石4级", 0, false));
            vipCoinThingScoreList.add(new VipCoinThingScore("强化石-强化石1级", 0, false));
            vipCoinThingScoreList.add(new VipCoinThingScore("强化石-强化石2级", 0, false));
            vipCoinThingScoreList.add(new VipCoinThingScore("强化石-强化石3级", 0, false));
            vipCoinThingScoreList.add(new VipCoinThingScore("强化石-强化石4级", 0, false));
            vipCoinThingScoreList.add(new VipCoinThingScore("强化石-强化石5级", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("戒指-天羽指环+0", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("戒指-天羽指环+1", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("戒指-天羽指环+2", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("戒指-天羽指环+3", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("戒指-天羽指环+4", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("戒指-天羽指环+5", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("戒指-幸运四叶指环+0", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("戒指-幸运四叶指环+1", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("戒指-幸运四叶指环+2", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("戒指-幸运四叶指环+3", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("戒指-幸运四叶指环+4", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("戒指-幸运四叶指环+5", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("戒指-浓缩烈焰指环+0", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("戒指-浓缩烈焰指环+1", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("戒指-浓缩烈焰指环+2", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("戒指-浓缩烈焰指环+3", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("戒指-浓缩烈焰指环+4", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("戒指-浓缩烈焰指环+5", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("戒指-浪漫之戒+0", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("戒指-浪漫之戒+1", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("戒指-浪漫之戒+2", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("戒指-浪漫之戒+3", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("戒指-浪漫之戒+4", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("戒指-蓝海指环+0", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("戒指-蓝海指环+1", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("戒指-蓝海指环+2", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("戒指-蓝海指环+3", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("戒指-蓝海指环+4", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("戒指-蓝海指环+5", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("手镯-恶魔之击+0", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("手镯-恶魔之击+1", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("手镯-恶魔之击+2", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("手镯-恶魔之击+3", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("手镯-恶魔之击+4", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("手镯-恶魔之击+5", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("手镯-敏锐之触+0", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("手镯-敏锐之触+1", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("手镯-敏锐之触+2", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("手镯-敏锐之触+3", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("手镯-敏锐之触+4", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("手镯-敏锐之触+5", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("手镯-沉稳之固+0", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("手镯-沉稳之固+1", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("手镯-沉稳之固+2", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("手镯-沉稳之固+3", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("手镯-沉稳之固+4", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("手镯-沉稳之固+5", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("手镯-祝福之握+0", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("手镯-祝福之握+1", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("手镯-祝福之握+2", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("手镯-祝福之握+3", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("手镯-祝福之握+4", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("手镯-祝福之握+5", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("服饰-原住民假面", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("服饰-巴罗夫的帽子", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("服饰-粉红啵咕帽", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("服饰-索兰恩之怒", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("服饰-绿色守护者", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("服饰-马迪亚斯导火索", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("服饰-马迪亚斯的披风", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("服饰-马迪亚斯的铠甲", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("道具-1级经验药水", 0, false));
            vipCoinThingScoreList.add(new VipCoinThingScore("道具-2级经验药水", 0, false));
            vipCoinThingScoreList.add(new VipCoinThingScore("道具-3级经验药水", 0, false));
            vipCoinThingScoreList.add(new VipCoinThingScore("道具-4级经验药水", 0, false));
            vipCoinThingScoreList.add(new VipCoinThingScore("道具-5级经验药水", 0, false));
            vipCoinThingScoreList.add(new VipCoinThingScore("道具-公会改名卡", 0, false));
            vipCoinThingScoreList.add(new VipCoinThingScore("道具-双倍功勋卡", 0, false));
            vipCoinThingScoreList.add(new VipCoinThingScore("道具-双倍经验卡", 0, false));
            vipCoinThingScoreList.add(new VipCoinThingScore("道具-变色卡", 0, false));
            vipCoinThingScoreList.add(new VipCoinThingScore("道具-幸运符15%", 0, false));
            vipCoinThingScoreList.add(new VipCoinThingScore("道具-幸运符25%", 0, false));
            vipCoinThingScoreList.add(new VipCoinThingScore("道具-改名卡", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("道具-神恩符", 0, false));
            vipCoinThingScoreList.add(new VipCoinThingScore("道具-防踢卡", 0, false));
            vipCoinThingScoreList.add(new VipCoinThingScore("项链-绿宝石吊坠", 0, false));
            vipCoinThingScoreList.add(new VipCoinThingScore("项链-龙之眼", 0, false));
            vipCoinThingScoreList.add(new VipCoinThingScore("项链-蓝色水滴", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("项链-女神之泪", 10, true));
            vipCoinThingScoreList.add(new VipCoinThingScore("项链-阿瑞斯的庇护", 10, true));

            return vipCoinThingScoreList;
        }
    }

    /**
     * 单条条件，隶属于配置下面。每个这个之间都是 或者 的关系。<br/>
     * 内部有细分的单条条件
     */
    @Data
    public static class VipCoinCondition implements Serializable {
        private static final long serialVersionUID = 4576903677653031081L;

        private List<SingleCondition> singleConditionList;

        public static List<VipCoinCondition> defaultConfigList() {
            List<VipCoinCondition> vipCoinConditionList = new ArrayList<>();

            VipCoinCondition vipCoinCondition1 = new VipCoinCondition();
            List<SingleCondition> singleConditionList1 = new ArrayList<>();
            vipCoinCondition1.setSingleConditionList(singleConditionList1);
            singleConditionList1.add(new SingleCondition(true, "极", "大于等于", 2));
            singleConditionList1.add(new SingleCondition(true, "", "大于等于", 12));
            vipCoinConditionList.add(vipCoinCondition1);

            VipCoinCondition vipCoinCondition2 = new VipCoinCondition();
            List<SingleCondition> singleConditionList2 = new ArrayList<>();
            vipCoinCondition2.setSingleConditionList(singleConditionList2);
            singleConditionList2.add(new SingleCondition(true, "极", "大于等于", 3));
            singleConditionList2.add(new SingleCondition(true, "", "大于等于", 11));
            vipCoinConditionList.add(vipCoinCondition2);
            return vipCoinConditionList;
        }
    }

    /**
     * contains = true, name = 极, compareType = ≥, num = 3 --> 包含 极 字样 数量 大于等于 3<br/>
     * contains = true, name = 极烈火, compareType = exist, num = 不需要 --> 包含 极烈火 字样<br/>
     * contains = false, name = 极神风, compareType = exist, num = 不需要 --> 不包含 极神风 字样<br/>
     * contains = true, name = null/空值, compareType = ≥, num = 11 --> 包含 “所有（有价值）” 物品 数量 大于等于 11<br/>
     */
    @Data
    public static class SingleCondition implements Serializable {
        private static final long serialVersionUID = 8863562726059452982L;

        /**
         * 包含/不包含
         */
        private Boolean contains;

        /**
         * 包含关键字
         */
        private String name;

        /**
         * 比较类型。大于等于小于，存在 等<br/>
         */
        private String compareType;

        /**
         * 比较数量
         */
        private Integer num;

        public SingleCondition() {
        }

        public SingleCondition(Boolean contains, String name, String compareType, Integer num) {
            this.contains = contains;
            this.name = name;
            this.compareType = compareType;
            this.num = num;
        }

        public List<String> splitName() {
            if (StringUtils.isBlank(name)) {
                return new ArrayList<>();
            }

            return Arrays.asList(this.name.split(""));
        }
    }

    @Data
    @NoArgsConstructor
    public static class VipCoinStopCondition implements Serializable {
        private static final long serialVersionUID = -2044471903959509516L;

        private String name;
        private Integer num;

        public VipCoinStopCondition(String name, Integer num) {
            this.name = name;
            this.num = num;
        }

        public static List<VipCoinStopCondition> defaultConfig() {
            List<VipCoinStopCondition> vipCoinStopConditionList = new ArrayList<>();
            vipCoinStopConditionList.add(new VipCoinStopCondition("极", 1));
            vipCoinStopConditionList.add(new VipCoinStopCondition("巴罗夫盾牌", 1));
            vipCoinStopConditionList.add(new VipCoinStopCondition("真·爱心", 1));
            vipCoinStopConditionList.add(new VipCoinStopCondition("真·天使", 1));
            vipCoinStopConditionList.add(new VipCoinStopCondition("真·远古", 1));
            vipCoinStopConditionList.add(new VipCoinStopCondition("+3", 2));
            vipCoinStopConditionList.add(new VipCoinStopCondition("+4", 1));
            vipCoinStopConditionList.add(new VipCoinStopCondition("+5", 1));
            vipCoinStopConditionList.add(new VipCoinStopCondition("女神之泪", 1));
            vipCoinStopConditionList.add(new VipCoinStopCondition("阿瑞斯", 1));
            vipCoinStopConditionList.add(new VipCoinStopCondition("强化石5级", 1));
            return vipCoinStopConditionList;
        }
    }
}
