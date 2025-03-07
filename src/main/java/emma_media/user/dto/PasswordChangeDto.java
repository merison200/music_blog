package emma_media.user.dto;

import lombok.Data;

@Data
public class PasswordChangeDto {

    private String email;
    private String oldPassword;
    private String newPassword;
}
