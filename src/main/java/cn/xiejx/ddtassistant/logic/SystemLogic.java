package cn.xiejx.ddtassistant.logic;

import cn.xiejx.ddtassistant.constant.GlobalVariable;
import cn.xiejx.ddtassistant.utils.Util;
import cn.xiejx.ddtassistant.vo.MemoryUseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.List;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/06/05 21:13
 */
@Component
@Slf4j
public class SystemLogic {
    private List<MemoryUseVo> memoryUseVoList;
    private MemoryMXBean memoryMXBean;

    @PostConstruct
    public void init() {
        memoryUseVoList = new ArrayList<>();
        memoryMXBean = ManagementFactory.getMemoryMXBean();

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
        MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        MemoryUsage nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();
        long nonHeapMemoryUsageUsed = nonHeapMemoryUsage.getUsed();
        long heapMemoryUsageUsed = heapMemoryUsage.getUsed();
        long totalUse = nonHeapMemoryUsageUsed + heapMemoryUsageUsed;
        long now = System.currentTimeMillis();
        memoryUseVoList.add(new MemoryUseVo(totalUse, now));
    }
}
