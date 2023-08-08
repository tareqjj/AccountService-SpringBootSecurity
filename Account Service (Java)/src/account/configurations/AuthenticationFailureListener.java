package account.configurations;

import account.services.LoginAttemptService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;

@Configuration
public class AuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {
    private final LoginAttemptService loginAttemptService;

    private final HttpServletRequest httpServletRequest;

    public AuthenticationFailureListener(LoginAttemptService loginAttemptService, HttpServletRequest httpServletRequest) {
        this.loginAttemptService = loginAttemptService;
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        final String username = event.getAuthentication().getName();
        if (username != null) {
            loginAttemptService.loginFailure(username, httpServletRequest.getRequestURI());
        }
    }
}
