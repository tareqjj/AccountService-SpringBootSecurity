package account.controllers;

import account.DTO.StatusDTO;
import account.DTO.UploadPayrollDTO;
import account.services.AccountantService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AccountantController {
    private final AccountantService accountantService;

    public AccountantController(AccountantService accountantService) {
        this.accountantService = accountantService;
    }

    @PostMapping("api/acct/payments")
    public StatusDTO uploadPayroll(@Valid @RequestBody List<UploadPayrollDTO> payrollList) {
        return accountantService.addPayroll(payrollList);
    }

    @PutMapping("api/acct/payments")
    public StatusDTO updatePayroll(@Valid @RequestBody UploadPayrollDTO payroll) {
        return accountantService.updatePayroll(payroll);
    }
}
