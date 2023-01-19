package cn.xiejx.ddtassistant.dto;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2023/01/17 21:21
 */
@Data
public class AbnormalDetectionDto {
    private Set<Integer> disconnectHwndSet = new HashSet<>();
    private Set<Integer> offsiteHwndSet = new HashSet<>();
    private Set<Integer> tokenExpiredHwndSet = new HashSet<>();
    private Set<Integer> leaveGameHwndSet = new HashSet<>();
    private Set<Integer> whiteScreenHwndSet = new HashSet<>();

    public boolean addDisconnect(int hwnd) {
        return disconnectHwndSet.add(hwnd);
    }

    public boolean addOffsite(int hwnd) {
        return offsiteHwndSet.add(hwnd);
    }

    public boolean addTokenExpired(int hwnd) {
        return tokenExpiredHwndSet.add(hwnd);
    }

    public boolean addLeaveGame(int hwnd) {
        return leaveGameHwndSet.add(hwnd);
    }

    public boolean addWhiteScreen(int hwnd) {
        return whiteScreenHwndSet.add(hwnd);
    }
}
