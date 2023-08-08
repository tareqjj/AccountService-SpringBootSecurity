package account.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name = "security_events")
public class SecurityEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @DateTimeFormat
    private LocalDate date;

    @NotBlank
    private String action;

    @NotBlank
    private String subject;

    @NotBlank
    private String object;

    @NotBlank
    private String path;

    public SecurityEvent() {
    }

    public SecurityEvent(String action, String subject, String object, String path) {
        this.date = LocalDate.now();
        this.action = action;
        this.subject = subject;
        this.object = object;
        this.path = path;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
