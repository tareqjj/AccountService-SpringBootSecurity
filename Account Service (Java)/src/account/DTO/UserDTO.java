package account.DTO;

import java.util.List;

public record UserDTO(Long id, String name, String lastname, String email, List<String> roles) {
}
