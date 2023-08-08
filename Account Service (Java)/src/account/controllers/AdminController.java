package account.controllers;

import account.DTO.*;
import account.services.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("api/admin/user/")
    public List<UserDTO> displayUsers(){
        return adminService.getAllUsers();
    }

    @DeleteMapping("api/admin/user/{email}")
    public DeleteUserDTO deleteUser(@PathVariable String email){
        return adminService.deleteUserByEmail(email);
    }

    @PutMapping("api/admin/user/role")
    public UserDTO updateUserRole(@RequestBody UpdateRoleDTO updateRoleDTO) {
        return adminService.changeUserRole(updateRoleDTO);
    }

    @PutMapping("api/admin/user/access")
    public StatusDTO changeUserLockStatus(@RequestBody SetLockStatusDTO setLockStatusDTO, HttpServletRequest request) {
        return adminService.setUserLock(setLockStatusDTO, request);
    }
}
