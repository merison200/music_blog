package emma_media.search.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponseDTO {

    private List<MusicDTO> musics;
    private List<BlogDTO> blogs;
    private List<VideoDTO> videos;
}


