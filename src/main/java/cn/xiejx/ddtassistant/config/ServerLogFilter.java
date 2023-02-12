package cn.xiejx.ddtassistant.config;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import cn.xiejx.ddtassistant.socket.WebSocketServer;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2023/02/11 16:47
 */
public class ServerLogFilter extends Filter<ILoggingEvent> {
    @Override
    public FilterReply decide(ILoggingEvent iLoggingEvent) {
        WebSocketServer.sendMessageAll(iLoggingEvent.getFormattedMessage());
        return FilterReply.NEUTRAL;
    }
}
