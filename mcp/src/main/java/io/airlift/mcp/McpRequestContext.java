package io.airlift.mcp;

import io.airlift.mcp.McpIdentity.Authenticated;
import io.airlift.mcp.model.InitializeRequest.ClientCapabilities;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

public interface McpRequestContext
{
    HttpServletRequest request();

    Authenticated<?> identity();

    void sendProgress(double progress, double total, String message);

    void sendMessage(String method, Optional<Object> params);

    ClientCapabilities clientCapabilities();
}
