// 
// Decompiled by Procyon v0.5.36
// 

package ir.am3n.needtool.webserver.sun;

import java.security.AccessController;


class ServerConfig {

    static int clockTick;
    static int defaultClockTick;
    static long defaultReadTimeout;
    static long defaultWriteTimeout;
    static long defaultIdleInterval;
    static long defaultSelCacheTimeout;
    static int defaultMaxIdleConnections;
    static long defaultMaxReqTime;
    static long defaultMaxRspTime;
    static long defaultTimerMillis;
    static long defaultDrainAmount;
    static long readTimeout;
    static long writeTimeout;
    static long idleInterval;
    static long selCacheTimeout;
    static long drainAmount;
    static int maxIdleConnections;
    static long maxReqTime;
    static long maxRspTime;
    static long timerMillis;
    static boolean debug;

    static long getReadTimeout() {
        return ServerConfig.readTimeout;
    }

    static long getSelCacheTimeout() {
        return ServerConfig.selCacheTimeout;
    }

    static boolean debugEnabled() {
        return ServerConfig.debug;
    }

    static long getIdleInterval() {
        return ServerConfig.idleInterval;
    }

    static int getClockTick() {
        return ServerConfig.clockTick;
    }

    static int getMaxIdleConnections() {
        return ServerConfig.maxIdleConnections;
    }

    static long getWriteTimeout() {
        return ServerConfig.writeTimeout;
    }

    static long getDrainAmount() {
        return ServerConfig.drainAmount;
    }

    static long getMaxReqTime() {
        return ServerConfig.maxReqTime;
    }

    static long getMaxRspTime() {
        return ServerConfig.maxRspTime;
    }

    static long getTimerMillis() {
        return ServerConfig.timerMillis;
    }

    static {
        ServerConfig.defaultClockTick = 10000;
        ServerConfig.defaultReadTimeout = 20L;
        ServerConfig.defaultWriteTimeout = 60L;
        ServerConfig.defaultIdleInterval = 300L;
        ServerConfig.defaultSelCacheTimeout = 120L;
        ServerConfig.defaultMaxIdleConnections = 200;
        ServerConfig.defaultMaxReqTime = 0L;
        ServerConfig.defaultMaxRspTime = 0L;
        ServerConfig.defaultTimerMillis = 0L;
        ServerConfig.defaultDrainAmount = 65536L;
        ServerConfig.debug = false;
        ServerConfig.idleInterval = AccessController.doPrivileged(new GetLongAction("ir.am3n.needtool.webserver.sun.idleInterval", ServerConfig.defaultIdleInterval)) * 1000L;
        ServerConfig.clockTick = AccessController.doPrivileged(new GetIntegerAction("ir.am3n.needtool.webserver.sun.clockTick", ServerConfig.defaultClockTick));
        ServerConfig.maxIdleConnections = AccessController.doPrivileged(new GetIntegerAction("ir.am3n.needtool.webserver.sun.maxIdleConnections", ServerConfig.defaultMaxIdleConnections));
        ServerConfig.readTimeout = AccessController.doPrivileged(new GetLongAction("ir.am3n.needtool.webserver.sun.readTimeout", ServerConfig.defaultReadTimeout)) * 1000L;
        ServerConfig.selCacheTimeout = AccessController.doPrivileged(new GetLongAction("ir.am3n.needtool.webserver.sun.selCacheTimeout", ServerConfig.defaultSelCacheTimeout)) * 1000L;
        ServerConfig.writeTimeout = AccessController.doPrivileged(new GetLongAction("ir.am3n.needtool.webserver.sun.writeTimeout", ServerConfig.defaultWriteTimeout)) * 1000L;
        ServerConfig.drainAmount = AccessController.doPrivileged(new GetLongAction("ir.am3n.needtool.webserver.sun.drainAmount", ServerConfig.defaultDrainAmount));
        ServerConfig.maxReqTime = AccessController.doPrivileged(new GetLongAction("ir.am3n.needtool.webserver.sun.maxReqTime", ServerConfig.defaultMaxReqTime));
        ServerConfig.maxRspTime = AccessController.doPrivileged(new GetLongAction("ir.am3n.needtool.webserver.sun.maxRspTime", ServerConfig.defaultMaxRspTime));
        ServerConfig.timerMillis = AccessController.doPrivileged(new GetLongAction("ir.am3n.needtool.webserver.sun.timerMillis", ServerConfig.defaultTimerMillis));
        ServerConfig.debug = AccessController.doPrivileged(new GetBooleanAction("ir.am3n.needtool.webserver.sun.debug"));
    }
}
