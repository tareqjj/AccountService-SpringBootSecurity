package account.repositories;

import account.models.SecurityEvent;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SecurityEventRepository extends CrudRepository<SecurityEvent, Long> {
    List<SecurityEvent> findAll();
}
