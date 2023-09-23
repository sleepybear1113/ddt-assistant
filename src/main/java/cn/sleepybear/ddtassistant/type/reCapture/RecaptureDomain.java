package cn.sleepybear.ddtassistant.type.reCapture;

import cn.sleepybear.ddtassistant.annotation.ReCaptureRequestMapping;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author sleepybear
 */
@Data
@NoArgsConstructor
public class RecaptureDomain {
    private String prefix;
    private String msg;
    private String value;

    public RecaptureDomain(ReCaptureRequestMapping reCaptureRequestMapping) {
        this.msg = reCaptureRequestMapping.msg();
        this.prefix = reCaptureRequestMapping.prefix();
        this.value = reCaptureRequestMapping.value()[0];
    }
}
