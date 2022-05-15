package cn.xiejx.ddtassistant.dm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author sleepybear
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class React {
    private int x;
    private int y;
    private String img;

    public boolean validReact() {
        return this.x > 0 && this.y > 0;
    }
}
