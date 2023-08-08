package account.services;

import account.DTO.StatusDTO;
import account.DTO.UploadPayrollDTO;
import account.models.Payroll;
import account.models.User;
import account.repositories.AccountantRepository;
import account.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

// calling save() to update an object inside a transactional method is not mandatory
@Service
@Transactional
public class AccountantService {
    private final AccountantRepository accountantRepository;
    private final UserRepository userRepository;

    public AccountantService(AccountantRepository accountantRepository, UserRepository userRepository) {
        this.accountantRepository = accountantRepository;
        this.userRepository = userRepository;
    }

    private User findUser(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }

    public StatusDTO addPayroll(List<UploadPayrollDTO> payrollList) {
        for (UploadPayrollDTO payrollRecord: payrollList) {
            User existingUser = findUser(payrollRecord.employee());
            if (existingUser == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employee does not exist!");
            else if (payrollRecord.salary() < 0)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Salary must be non negative!");
            else if (accountantRepository.findByUserAndPeriod(existingUser, payrollRecord.period()) != null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Period exist!");
            else {

                Payroll newPayroll = new Payroll();
                newPayroll.setUser(existingUser);
                newPayroll.setPeriod(payrollRecord.period());
                newPayroll.setSalary(payrollRecord.salary());
                accountantRepository.save(newPayroll);
            }
        }
        return new StatusDTO("Added successfully!");
    }

    public StatusDTO updatePayroll(UploadPayrollDTO payroll) {
        User existingUser = findUser(payroll.employee());
        Payroll existingPayroll = accountantRepository.findByUserAndPeriod(existingUser, payroll.period());
        if (existingPayroll == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employee does not exist!");
        else if (payroll.salary() < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Salary must be non negative!");
        else {
            existingPayroll.setSalary(payroll.salary());
            accountantRepository.save(existingPayroll);
            return new StatusDTO("Updated successfully!");
        }
    }
}
