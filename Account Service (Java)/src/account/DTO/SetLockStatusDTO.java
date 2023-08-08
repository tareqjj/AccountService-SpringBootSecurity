package account.DTO;

import jakarta.validation.constraints.NotBlank;

public record SetLockStatusDTO(@NotBlank String user, String operation) {
}
