package account.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.YearMonth;

@Entity
@Table(name = "payrolls", uniqueConstraints = { @UniqueConstraint(name = "UniqueUserAndPeriod", columnNames = {"user_id", "period"}) })
public class Payroll {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;

    @PositiveOrZero(message = "Salary must be non negative!")
    private long salary;

    @JsonFormat(pattern="MM-yyyy")
    private YearMonth period;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Payroll() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSalary() {
        return salary;
    }

    public void setSalary(long salary) {
        this.salary = salary;
    }

    public YearMonth getPeriod() {
        return period;
    }

    public void setPeriod(YearMonth period) {
        this.period = period;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static String yearMonthFormat(String date) {
        String month = date.substring(0,2);
        String year = date.substring(3);
        return year + "-" + month;
    }
}
