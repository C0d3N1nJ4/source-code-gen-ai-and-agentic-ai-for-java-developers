package com.acme.assistant.chapter11.mcp;

import java.math.BigDecimal;
import java.util.Map;

/**
 * MCP contract for finance capabilities.
 */
public interface FinanceMcpClient {

    Map<String, Object> estimateTravelExpenses(String destination, int days);

    Map<String, Object> createExpenseClaim(String userId, String category, BigDecimal amount);
}
