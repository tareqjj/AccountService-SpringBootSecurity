package account.services;

import account.DTO.NewPasswordDTO;

import account.DTO.UserDTO;
import account.logger.AppLogger;
import account.models.Payroll;
import account.models.User;
import account.repositories.AccountantRepository;
import account.repositories.GroupRepository;
import account.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class UserService {
    private final UserRepository userRepository;

    private final AccountantRepository accountantRepository;

    private final GroupRepository groupRepository;

    private final PasswordEncoder encoder;

    private final AppLogger appLogger;

    private final List<String> breachedList = Arrays.asList("PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch",
            "PasswordForApril", "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust",
            "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember");


    public UserService(UserRepository userRepository, AccountantRepository accountantRepository,
                       GroupRepository groupRepository, AppLogger appLogger, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.accountantRepository  = accountantRepository;
        this.groupRepository = groupRepository;
        this.appLogger = appLogger;
        this.encoder = encoder;
    }

    private User findUser(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }

    private List<Object> PayrollprocessingDTO(List<Payroll> payrollList) {
        List<Object> payrollJSON = new ArrayList<>();
        for (Payroll payrollPeriod : payrollList) {
            HashMap<String, String> userPayroll = new LinkedHashMap<>();
            userPayroll.put("name", payrollPeriod.getUser().getName());
            userPayroll.put("lastname", payrollPeriod.getUser().getLastname());
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MMMM-yyyy");
            userPayroll.put("period", payrollPeriod.getPeriod().format(dateFormat));
            String dollar = Long.toString(payrollPeriod.getSalary() / 100);
            String cent = Long.toString(payrollPeriod.getSalary() % 100);
            userPayroll.put("salary",String.format("%s dollar(s) %s cent(s)", dollar, cent));
            payrollJSON.add(userPayroll);
        }
        return payrollJSON;
    }

    public static UserDTO createUserDTO(User user) {
        List<String> userGroupsStringList = new ArrayList<>();
        user.getUserGroups().forEach(group -> userGroupsStringList.add(group.getRole()));
        return new UserDTO(user.getId(), user.getName(), user.getLastname(), user.getEmail(), userGroupsStringList);
    }

    public ResponseEntity<UserDTO> CreateUser(User user) {
        if (findUser(user.getEmail()) != null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User exist!");
        else if (breachedList.contains(user.getPassword()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The password is in the hacker's database!");
        else {
            if (userRepository.findAll().isEmpty())
                user.setUserGroups(groupRepository.findByRole("ROLE_ADMINISTRATOR"));
            else
                user.setUserGroups(groupRepository.findByRole("ROLE_USER"));
            user.setEmail(user.getEmail().toLowerCase());
            user.setPassword(encoder.encode(user.getPassword()));
            user.setAccountNonLocked(true);
            userRepository.save(user);
            appLogger.createUser(user.getEmail());
            return new ResponseEntity<>(createUserDTO(user), HttpStatus.OK);
        }
    }

    // You can return Object directly without ResponseEntity
    public ResponseEntity<Object> getPayroll(String email, String period) {
        User user = findUser(email);
        List<Payroll> userPayrollList = accountantRepository.findByUserOrderByPeriodDesc(user);
        if (period == null)
            return new ResponseEntity<>(PayrollprocessingDTO(userPayrollList), HttpStatus.OK);
        else if (!period.matches("^(0?[1-9]|[1][0-2])-\\d\\d\\d\\d$"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Period must valid format MM-yyyy");
        else {
            YearMonth yearMonthPeriod = YearMonth.parse(Payroll.yearMonthFormat(period));
            for (Payroll payrollRecord: userPayrollList) {
                if (payrollRecord.getPeriod().equals(yearMonthPeriod))
                    return new ResponseEntity<>(PayrollprocessingDTO(List.of(payrollRecord)).get(0), HttpStatus.OK);
            }
            return new ResponseEntity<>(PayrollprocessingDTO(List.of()), HttpStatus.OK);
        }
    }

    public ResponseEntity<NewPasswordDTO> updatePassword(UserDetails userDetails, String newPassword) {
        if (breachedList.contains(newPassword))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The password is in the hacker's database!");
        else if (encoder.matches(newPassword, userDetails.getPassword()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The passwords must be different!");
        else if (newPassword.length() <=  11)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password length must be 12 chars minimum!");
        else {
            User user = findUser(userDetails.getUsername());
            user.setPassword(encoder.encode(newPassword));
            userRepository.save(user);
            appLogger.changePassword(user.getEmail());
            NewPasswordDTO newPasswordDTO = new NewPasswordDTO(user.getEmail());
            return new ResponseEntity<>(newPasswordDTO, HttpStatus.OK);
        }
    }



}
