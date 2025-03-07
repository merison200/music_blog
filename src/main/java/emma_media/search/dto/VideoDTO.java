package emma_media.search.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoDTO {

    private Long id;
    private String title;
    private String description;
    private String fileUrl;
    private String createdAt;
    private String updatedAt;
}
