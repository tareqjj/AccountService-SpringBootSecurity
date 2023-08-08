package account.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PasswordDTO {
    @JsonProperty(value = "new_password")
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
