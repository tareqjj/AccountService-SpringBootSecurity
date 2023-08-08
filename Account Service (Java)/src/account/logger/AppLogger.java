package account.logger;

import account.models.SecurityEvent;
import account.repositories.SecurityEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AppLogger {
    private final SecurityEventRepository securityEventRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(AppLogger.class);

    public AppLogger(SecurityEventRepository securityEventRepository) {
        this.securityEventRepository = securityEventRepository;
    }


    public void createUser(String object) {
        SecurityEvent securityEvent = new SecurityEvent(
                "CREATE_USER",
                "Anonymous",
                object,
                "/api/auth/signup");
        securityEventRepository.save(securityEvent);
        LOGGER.info("A user has been successfully registered");
    }

    public void changePassword(String subject_object) {
        SecurityEvent securityEvent = new SecurityEvent(
                "CHANGE_PASSWORD",
                subject_object,
                subject_object,
                "/api/auth/changepass");
        securityEventRepository.save(securityEvent);
        LOGGER.info("A user has changed the password successfully");
    }

    public void accessDenied(String subject, String object) {
        SecurityEvent securityEvent = new SecurityEvent(
                "ACCESS_DENIED",
                subject,
                object,
                "/api/acct/payments"
        );
        securityEventRepository.save(securityEvent);
        LOGGER.info("ACCESS_DENIED");
    }

    public void LoginFailed(String subject, String object) {
        SecurityEvent securityEvent = new SecurityEvent(
                "LOGIN_FAILED",
                subject,
                object,
                "/api/empl/payment"
        );
        securityEventRepository.save(securityEvent);
        LOGGER.info("Failed authentication");
    }

    public void grantRole(String subject, String object, String role) {
        SecurityEvent securityEvent = new SecurityEvent(
                "GRANT_ROLE",
                subject,
                String.format("Grant role %s to %s", role, object),
                "/api/admin/user/role"
        );
        securityEventRepository.save(securityEvent);
        LOGGER.info("A role is granted to a user");
    }

    public void removeRole(String subject, String object, String role) {
        SecurityEvent securityEvent = new SecurityEvent(
                "REMOVE_ROLE",
                subject,
                String.format("Remove role %s from %s", role, object),
                "/api/admin/user/role"
        );
        securityEventRepository.save(securityEvent);
        LOGGER.info("A role has been revoked");
    }

    public void lockUser(String subject, String path) {
        SecurityEvent securityEvent = new SecurityEvent(
                "LOCK_USER",
                subject,
                String.format("Lock user %s", subject),
                path
        );
        securityEventRepository.save(securityEvent);
        LOGGER.info("The Administrator has locked the user");
    }

    public void unlockedUser(String subject, String object) {
        SecurityEvent securityEvent = new SecurityEvent(
                "UNLOCK_USER",
                subject,
                String.format("Unlock user %s", object),
                "/api/admin/user/access"
        );
        securityEventRepository.save(securityEvent);
        LOGGER.info("The Administrator has unlocked a user");
    }

    public void deleteUser(String subject, String object) {
        SecurityEvent securityEvent = new SecurityEvent(
                "DELETE_USER",
                subject,
                object,
                "/api/admin/user"
        );
        securityEventRepository.save(securityEvent);
        LOGGER.info("The Administrator has deleted a user");
    }

    public void bruteForce(String subject, String object) {
        SecurityEvent securityEvent = new SecurityEvent(
                "BRUTE_FORCE",
                subject,
                object,
                "/api/empl/payment"
        );
        securityEventRepository.save(securityEvent);
        LOGGER.info("A user has been blocked on suspicion of a brute force attack");
    }
}
