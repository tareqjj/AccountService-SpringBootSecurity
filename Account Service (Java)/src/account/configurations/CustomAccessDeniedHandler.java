package account.configurations;

import account.logger.AppLogger;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

@Configuration
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Autowired
    private AppLogger appLogger;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exc)
            throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        appLogger.accessDenied(auth.getName(), request.getRequestURI());
        response.sendError(403, "Access Denied!");
    }
}