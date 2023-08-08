package account.DTO;

public record NewPasswordDTO(String email, String status) {
    public NewPasswordDTO(String email) {
        this(email,  "The password has been updated successfully");
    }
}
