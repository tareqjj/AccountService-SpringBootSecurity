package account.services;

import account.models.SecurityEvent;
import account.repositories.SecurityEventRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditorService {
    private final SecurityEventRepository securityEventRepository;

    public AuditorService(SecurityEventRepository securityEventRepository) {
        this.securityEventRepository = securityEventRepository;
    }

    public List<SecurityEvent> getAllEvents() {
        List<SecurityEvent> securityEventList = securityEventRepository.findAll();
        if (securityEventList.isEmpty())
            return List.of();
        return securityEventList;
    }
}
