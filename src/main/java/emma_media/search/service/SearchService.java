package emma_media.search.service;

import emma_media.blog.repository.BlogRepository;
import emma_media.music.repository.MusicRepository;
import emma_media.search.dto.BlogDTO;
import emma_media.search.dto.MusicDTO;
import emma_media.search.dto.SearchResponseDTO;
import emma_media.search.dto.VideoDTO;
import emma_media.video.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private MusicRepository musicRepository;

    @Autowired
    private VideoRepository videoRepository;

    public SearchResponseDTO search(String keyword) {
        List<BlogDTO> blogs = blogRepository.searchBlogs(keyword)
                .stream()
                .map(blog -> new BlogDTO(
                        blog.getId(),
                        blog.getTitle(),
                        blog.getContent(),
                        blog.getImageUrl(),
                        blog.getCreatedAt().toString(),
                        blog.getUpdatedAt().toString()
                ))
                .collect(Collectors.toList());

        List<VideoDTO> videos = videoRepository.searchVideos(keyword)
                .stream()
                .map(video -> new VideoDTO(
                        video.getId(),
                        video.getTitle(),
                        video.getDescription(),
                        video.getFileUrl(),
                        video.getCreatedAt().toString(),
                        video.getUpdatedAt().toString()
                ))
                .collect(Collectors.toList());

        List<MusicDTO> musics = musicRepository.searchMusic(keyword)
                .stream()
                .map(music -> new MusicDTO(
                        music.getId(),
                        music.getTitle(),
                        music.getArtist(),
                        music.getFileUrl(),
                        music.getCreatedAt().toString(),
                        music.getUpdatedAt().toString()
                ))
                .collect(Collectors.toList());

        return new SearchResponseDTO(musics, blogs, videos);
    }
}