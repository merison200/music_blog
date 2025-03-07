package emma_media.user.dto;

import lombok.Data;

@Data
public class LoginResponseDto {

    private String username;
    private String email;
    private String token;

    public LoginResponseDto(String username, String email, String token) {
       this.username = username;
       this.email = email;
       this.token = token;
    }
}
