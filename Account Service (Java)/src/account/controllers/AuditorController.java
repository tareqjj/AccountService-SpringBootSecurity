package account.controllers;

import account.models.SecurityEvent;
import account.services.AuditorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AuditorController {
    private final AuditorService auditorService;

    public AuditorController(AuditorService auditorService) {
        this.auditorService = auditorService;
    }

    @GetMapping("api/security/events/")
    public List<SecurityEvent> displayEvents() {
        return auditorService.getAllEvents();
    }
}
