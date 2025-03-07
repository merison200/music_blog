package emma_media.video.controller;

import emma_media.video.dto.VideoResponseDto;
import emma_media.video.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/video")
@RequiredArgsConstructor
public class VideoController {

    @Autowired
    private VideoService videoService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<VideoResponseDto> createVideo(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("file") MultipartFile file,
            Principal principal) {

        String userEmail = principal.getName();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(videoService.createVideo(title, description, file, userEmail));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VideoResponseDto> getVideoById(@PathVariable Long id) {
        return ResponseEntity.ok(videoService.getVideoById(id));
    }

    @GetMapping
    public ResponseEntity<List<VideoResponseDto>> getAllVideos() {
        return ResponseEntity.ok(videoService.getAllVideos());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVideo(@PathVariable Long id, Principal principal) {
        String userEmail = principal.getName();
        videoService.deleteVideo(id, userEmail);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-video")
    public ResponseEntity<List<VideoResponseDto>> getVideoByUser() {
        List<VideoResponseDto> videoList = videoService.getVideoByUser();
        return ResponseEntity.ok(videoList);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<VideoResponseDto> updateVideo(
            @PathVariable Long id,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "file", required = false) MultipartFile file) {

        VideoResponseDto updatedVideo = videoService.updateVideo(id, title, description, file);
        return ResponseEntity.ok(updatedVideo);
    }
}
