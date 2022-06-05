package cn.xiejx.ddtassistant.logic;

import cn.xiejx.ddtassistant.constant.GlobalVariable;
import cn.xiejx.ddtassistant.utils.Util;
import cn.xiejx.ddtassistant.vo.MemoryUseVo;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/06/05 21:13
 */
@Component
public class SystemLogic {
    private List<MemoryUseVo> memoryUseVoList;
    private Runtime runtime;

    @PostConstruct
    public void init() {
        memoryUseVoList = new ArrayList<>();
        runtime = Runtime.getRuntime();
        addMemory();

        GlobalVariable.THREAD_POOL.execute(this::monitor);
    }

    public List<MemoryUseVo> getMemoryUse() {
        addMemory();
        return memoryUseVoList;
    }

    private void monitor() {
        long delay = 1000L * 60 * 2;
        while (true) {
            Util.sleep(delay);
            addMemory();
        }
    }

    private void addMemory() {
        long totalMemory = runtime.totalMemory();
        long now = System.currentTimeMillis();
        memoryUseVoList.add(new MemoryUseVo(totalMemory, now));
    }
}
