package io.airlift.mcp.operations;

import com.google.inject.Inject;
import io.airlift.mcp.McpRequestContext;
import io.airlift.mcp.model.LoggingLevel;
import io.airlift.mcp.model.LoggingMessageNotification;
import io.airlift.mcp.sessions.SessionController;
import io.airlift.mcp.sessions.SessionId;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

import static io.airlift.mcp.McpException.exception;
import static io.airlift.mcp.model.Constants.MCP_SESSION_ID;
import static io.airlift.mcp.model.Constants.NOTIFICATION_MESSAGE;
import static io.airlift.mcp.sessions.SessionValueKey.LOGGING_LEVEL;
import static java.util.Objects.requireNonNull;

public class LegacyLogger
{
    private final Optional<SessionController> sessionController;

    @Inject
    public LegacyLogger(Optional<SessionController> sessionController)
    {
        this.sessionController = requireNonNull(sessionController, "sessionController is null");
    }

    public void sendLog(McpRequestContext requestContext, LoggingLevel level, String message)
    {
        if (level.level() >= currentLoggingLevel(requestContext.request()).level()) {
            LoggingMessageNotification logNotification = new LoggingMessageNotification(level, Optional.empty(), Optional.of(message));
            requestContext.sendMessage(NOTIFICATION_MESSAGE, Optional.of(logNotification));
        }
    }

    private LoggingLevel currentLoggingLevel(HttpServletRequest request)
    {
        SessionController localSessionController = sessionController.orElseThrow(() -> new IllegalStateException("Sessions not enabled"));
        SessionId sessionId = requireSessionId(request);

        return localSessionController.getSessionValue(sessionId, LOGGING_LEVEL).orElseThrow(() -> exception("Session is invalid"));
    }

    private static SessionId requireSessionId(HttpServletRequest request)
    {
        return optionalSessionId(request).orElseThrow(() -> exception("Missing %s header in request".formatted(MCP_SESSION_ID)));
    }

    private static Optional<SessionId> optionalSessionId(HttpServletRequest request)
    {
        return Optional.ofNullable(request.getHeader(MCP_SESSION_ID))
                .map(SessionId::new);
    }
}
