package account.services;

import account.logger.AppLogger;
import account.models.User;
import account.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class LoginAttemptService {
    public static final int MAX_FAILED_ATTEMPTS = 4;
    private final UserRepository userRepository;
    private final AppLogger appLogger;

    public LoginAttemptService(UserRepository userRepository, AppLogger appLogger) {
        this.userRepository = userRepository;
        this.appLogger = appLogger;
    }

    public void loginSuccess(String email) {
        User user = userRepository.findByEmailIgnoreCase(email);
        user.setFailedAttempt(0);
        userRepository.save(user);
    }

    public void loginFailure(String email, String uri) {
        appLogger.LoginFailed(email, uri);
        User existingUser = userRepository.findByEmailIgnoreCase(email);
        if (existingUser == null)
            return; //returns 401 bad credentials response by default, throw exception will return 500 code
        existingUser.getUserGroups().forEach(group -> {
            if (group.getRole().equals("ROLE_ADMINISTRATOR"))
                existingUser.setFailedAttempt(0);
        });
        existingUser.setFailedAttempt(existingUser.getFailedAttempt() + 1);
        if (existingUser.getFailedAttempt() > MAX_FAILED_ATTEMPTS) {
            existingUser.setAccountNonLocked(false);
            appLogger.bruteForce(existingUser.getEmail(), uri);
            appLogger.lockUser(existingUser.getEmail(), uri);
        }
        userRepository.save(existingUser);
    }
}
