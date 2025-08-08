package com.aiadtech.collection.util;

import org.slf4j.MDC;

import java.util.Optional;

/**
 * MDC工具类
 */
public class Slf4jMDCUtil {

    public static final String TRACE_ID = "TRACE-ID";

    public static final String CHAIN_ID = "X-CHAIN-ID";

    private Slf4jMDCUtil() {
    }

    /**
     * 更新设置链路追踪ID TRACE-ID + X-CHAIN-ID
     *
     * @param chainId 指定 X-CHAIN-ID，传入null时则使用 TRACE-ID 表示
     */
    public static void upsert(String chainId) {
        var traceId = StringUtil.shortUUID();

        // 追踪ID，用于当前服务（不含上下游服务）链路追踪
        MDC.put(TRACE_ID, traceId);

        // 链路ID，调用端如果有在 HTTP HEADER 指定则直接使用，用于分布式系统上下游服务链路追踪，若没指定则使用traceId
        MDC.put(CHAIN_ID, Optional.ofNullable(chainId).orElse(traceId));
    }

    public static void upsert() {
        upsert(null);
    }

    public static void clear() {
        MDC.clear();
    }

    public static String getCurrentChainId() {
        return MDC.get(CHAIN_ID);
    }

}
