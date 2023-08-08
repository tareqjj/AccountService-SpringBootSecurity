package account.controllers;

import account.DTO.NewPasswordDTO;
import account.DTO.PasswordDTO;
import account.DTO.UserDTO;
import account.models.User;
import account.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("api/auth/signup")
    public ResponseEntity<UserDTO> signup(@Valid @RequestBody User user) {
        return userService.CreateUser(user);
    }

    //Getting authenticated user details using @AuthenticationPrincipal
    @GetMapping("api/empl/payment")
    public ResponseEntity<Object> displayPayroll(@Valid @RequestParam(required = false) String period, @AuthenticationPrincipal UserDetails authenticatedUser) {
        return userService.getPayroll(authenticatedUser.getUsername(), period);
    }

    //Different method of getting authenticated user details using Authentication interface
    @PostMapping("api/auth/changepass")
    public ResponseEntity<NewPasswordDTO> changePassword(@RequestBody PasswordDTO newPassword, Authentication authenticatedUser) {
        UserDetails user = (UserDetails) authenticatedUser.getPrincipal();
        return userService.updatePassword(user, newPassword.getPassword());
    }
}
