package emma_media.contact.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactMessageDTO {

    private String name;
    private String phone;
    private String email;
    private String message;
}