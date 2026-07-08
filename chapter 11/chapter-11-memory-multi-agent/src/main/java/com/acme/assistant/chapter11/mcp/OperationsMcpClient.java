package com.acme.assistant.chapter11.mcp;

import java.util.Map;

/**
 * MCP contract for operations capabilities.
 */
public interface OperationsMcpClient {

    Map<String, Object> restartServer(String environment, String serviceName);

    Map<String, Object> health(String serviceName);
}
