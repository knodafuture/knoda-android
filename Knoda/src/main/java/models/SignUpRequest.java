package models;

/**
 * Created by nick on 1/20/14.
 */
public class SignUpRequest extends LoginRequest {

    public String email;
    public String username;

    public SignUpRequest(String email, String username, String password) {
        super(username, password);
        this.email = email;
        this.username = username;
        this.password = password;
    }
}
