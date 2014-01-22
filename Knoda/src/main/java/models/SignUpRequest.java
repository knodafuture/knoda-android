package models;

/**
 * Created by nick on 1/20/14.
 */
public class SignUpRequest extends BaseModel {

    public String email;
    public String username;
    public String password;

    public SignUpRequest(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }
}
