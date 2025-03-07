package emma_media.search.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MusicDTO {

    private Long id;
    private String title;
    private String artist;
    private String fileUrl;
    private String createdAt;
    private String updatedAt;
}
