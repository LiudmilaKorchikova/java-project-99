package hexlet.code.app.model;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
    private String token;
    private String redirectUrl;

    public LoginResponse(String token) {
        this.token = token;
    }
}
