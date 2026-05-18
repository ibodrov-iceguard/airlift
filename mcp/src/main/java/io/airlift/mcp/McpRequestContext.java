package io.airlift.mcp;

import io.airlift.mcp.McpIdentity.Authenticated;
import io.airlift.mcp.model.InitializeRequest.ClientCapabilities;
import io.airlift.mcp.model.JsonRpcResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

public interface McpRequestContext
{
    HttpServletRequest request();

    Authenticated<?> identity();

    void sendProgress(double progress, double total, String message);

    void sendMessage(String method, Optional<Object> params);

    ClientCapabilities clientCapabilities();

    /**
     * Sends a server-to-client request and waits for the response until given timeout. NOTE - may not be supported depending on how you've configured MCP - see the README regarding Sessions and Storage
     */
    <R> JsonRpcResponse<R> serverToClientRequest(String method, Object params, Class<R> responseType, Duration timeout, Duration pollInterval)
            throws InterruptedException, TimeoutException;
}
