package cn.xiejx.ddtassistant.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/06/06 16:41
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyString implements Serializable {
    private static final long serialVersionUID = 6419260314639157015L;
    private String string;
}
