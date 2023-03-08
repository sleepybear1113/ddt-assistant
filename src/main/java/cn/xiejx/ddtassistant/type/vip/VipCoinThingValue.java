package cn.xiejx.ddtassistant.type.vip;

import lombok.Data;

import java.io.Serializable;

/**
 * VIP 币物品和价值的对应关系
 *
 * @author sleepybear
 * @date 2023/03/05 23:26
 */
@Data
public class VipCoinThingValue implements Serializable {
    private static final long serialVersionUID = -8073907874896809580L;

    private String name;
    private Integer value;
    private Boolean enable;
}
