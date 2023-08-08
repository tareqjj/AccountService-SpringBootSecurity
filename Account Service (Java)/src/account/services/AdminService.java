package account.services;

import account.DTO.*;
import account.logger.AppLogger;
import account.models.Group;
import account.models.User;
import account.repositories.GroupRepository;
import account.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminService {
    private final UserRepository userRepository;

    private final GroupRepository groupRepository;

    private final AppLogger appLogger;

    public AdminService(UserRepository userRepository, GroupRepository groupRepository, AppLogger appLogger) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.appLogger = appLogger;
    }

    private String getAuthenticatedUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    private void grantRole(User existingUser,List<Group> roleToUpdate, String rolePrefix) {
        existingUser.getUserGroups().forEach(group -> {
            if (group.getRole().equals("ROLE_ADMINISTRATOR"))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user cannot combine administrative and business roles!");
            if (group.getRole().equals("ROLE_USER") || group.getRole().equals("ROLE_ACCOUNTANT"))
                if (rolePrefix.equals("ROLE_ADMINISTRATOR"))
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user cannot combine administrative and business roles!");
        });
        List<Group> rolesAfterGrant = new ArrayList<>(existingUser.getUserGroups());
        roleToUpdate.addAll(rolesAfterGrant);
        existingUser.setUserGroups(roleToUpdate);
        userRepository.save(existingUser);
    }

    private void removeRole(User existingUser,List<Group> roleToUpdate, String rolePrefix) {
        for (Group group : existingUser.getUserGroups()) {
            if (group.getRole().equals("ROLE_ADMINISTRATOR"))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");
            else if(group.getRole().equals(rolePrefix)) {
                if (existingUser.getUserGroups().size() == 1)
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user must have at least one role!");
                List<Group> rolesAfterRemove = new ArrayList<>(existingUser.getUserGroups()); //New list to circumvent List Fail-Fast
                rolesAfterRemove.removeAll(roleToUpdate);
                existingUser.setUserGroups(rolesAfterRemove);
                userRepository.save(existingUser);
                break;
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user does not have a role!");
        }
    }

    public List<UserDTO> getAllUsers() {
        List<UserDTO> userDTOList = new ArrayList<>();
        userRepository.findAll().forEach(user -> userDTOList.add(UserService.createUserDTO(user)));
        return userDTOList;
    }

    public DeleteUserDTO deleteUserByEmail(String email) {
        User existingUser = userRepository.findByEmailIgnoreCase(email);
        if (existingUser == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        existingUser.getUserGroups().forEach(group -> {
            if (group.getRole().equals("ROLE_ADMINISTRATOR"))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");
        });
        userRepository.delete(existingUser);
        appLogger.deleteUser(getAuthenticatedUsername(),existingUser.getEmail());
        return new DeleteUserDTO(email);
    }

    public UserDTO changeUserRole(UpdateRoleDTO updateRoleDTO) {
        String rolePrefix= "ROLE_" + updateRoleDTO.role();
        User existingUser = userRepository.findByEmailIgnoreCase(updateRoleDTO.user());
        List<Group> roleToUpdate = groupRepository.findByRole(rolePrefix);
        if (existingUser == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        else if (roleToUpdate.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found!");
        else if (updateRoleDTO.operation().equals("GRANT")) {
            grantRole(existingUser,roleToUpdate, rolePrefix);
            appLogger.grantRole(getAuthenticatedUsername(), existingUser.getEmail(), updateRoleDTO.role());
            return UserService.createUserDTO(existingUser);
        }
        else if (updateRoleDTO.operation().equals("REMOVE")) {
            removeRole(existingUser, roleToUpdate, rolePrefix);
            appLogger.removeRole(getAuthenticatedUsername(), existingUser.getEmail(), updateRoleDTO.role());
            return UserService.createUserDTO(existingUser);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Operation is not valid!");
    }

    public StatusDTO setUserLock(SetLockStatusDTO setLockStatusDTO, HttpServletRequest request) {
        User existingUser = userRepository.findByEmailIgnoreCase(setLockStatusDTO.user());
        if (existingUser == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        existingUser.getUserGroups().forEach(group -> {
            if (group.getRole().equals("ROLE_ADMINISTRATOR"))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't lock the ADMINISTRATOR!");
        });
        if (setLockStatusDTO.operation().equals("LOCK")) {
            existingUser.setAccountNonLocked(false);
            userRepository.save(existingUser);
            appLogger.lockUser(existingUser.getEmail(), request.getRequestURI());
            return new StatusDTO(String.format("User %s locked!", existingUser.getEmail()));
        }
        else if (setLockStatusDTO.operation().equals("UNLOCK")) {
            existingUser.setFailedAttempt(0);
            existingUser.setAccountNonLocked(true);
            userRepository.save(existingUser);
            appLogger.unlockedUser(getAuthenticatedUsername(), existingUser.getEmail());
            return new StatusDTO(String.format("User %s unlocked!", existingUser.getEmail()));
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Operation is not valid!");
    }
}
