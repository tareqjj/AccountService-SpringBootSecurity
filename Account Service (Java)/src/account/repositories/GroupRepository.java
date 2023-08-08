package account.repositories;

import account.models.Group;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends CrudRepository<Group, Long> {

    List<Group> findAll();
    List<Group> findByRole(String role);
}
