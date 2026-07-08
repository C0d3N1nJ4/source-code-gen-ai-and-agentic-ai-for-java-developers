package com.acme.assistant.chapter11.mcp;

import java.util.Map;

/**
 * MCP contract for HR capabilities.
 */
public interface HrMcpClient {

    Map<String, Object> submitLeave(String userId, String leaveDate, String reason);

    Map<String, Object> lookupEmployee(String employeeId);
}
