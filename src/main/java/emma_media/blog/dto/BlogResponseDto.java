package emma_media.blog.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BlogResponseDto {

    private Long blogId;
    private String title;
    private String content;
    private String imageUrl;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
