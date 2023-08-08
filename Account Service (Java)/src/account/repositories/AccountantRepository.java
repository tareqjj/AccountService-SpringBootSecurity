package account.repositories;

import account.models.Payroll;
import account.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;

@Repository
public interface AccountantRepository extends CrudRepository<Payroll, Long> {
    Payroll findByUserAndPeriod(User user, YearMonth period);
    List<Payroll> findByUserOrderByPeriodDesc(User user);
}
