package cn.sleepybear.ddtassistant.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/06/05 21:12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemoryUseVo implements Serializable {
    @Serial
    private static final long serialVersionUID = 7022238780238544826L;

    private Long memory;
    private Long time;
}
