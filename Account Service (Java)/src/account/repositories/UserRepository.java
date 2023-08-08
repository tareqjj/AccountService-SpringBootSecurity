package account.repositories;

import account.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    User findByEmailIgnoreCase(String email);
    List<User> findAll();
}
