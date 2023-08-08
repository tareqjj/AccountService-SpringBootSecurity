package account.DTO;

public record DeleteUserDTO(String user, String status) {

    public DeleteUserDTO(String user) {
        this(user, "Deleted successfully!");
    }
}
