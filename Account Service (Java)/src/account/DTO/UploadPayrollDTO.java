package account.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.YearMonth;
// @jsonFormat to pass period as YearMonth and validation on valid dates
public record UploadPayrollDTO(String employee, @JsonFormat(pattern="MM-yyyy") YearMonth period, Long salary) {
}
