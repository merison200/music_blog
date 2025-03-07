package emma_media.video.service;

import emma_media.user.dto.UserResponseDto;
import emma_media.user.entity.User;
import emma_media.user.repository.UserRepository;
import emma_media.user.service.UserService;
import emma_media.video.dto.VideoResponseDto;
import emma_media.video.entity.Video;
import emma_media.video.exception.ResourceNotFoundException;
import emma_media.video.exception.UnauthorizedException;
import emma_media.video.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VideoService {

    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;


    public VideoResponseDto createVideo(String title, String description, MultipartFile file, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new emma_media.video.exception.UserNotFoundException("User not found"));

        String fileUrl = saveFile(file);

        Video video = new Video();
        video.setTitle(title);
        video.setDescription(description);
        video.setFileUrl(fileUrl);
        video.setUser(user);

        Video savedVideo = videoRepository.save(video);
        return mapToResponseDto(savedVideo);
    }

    public List<VideoResponseDto> getVideoByUser() {
        UserResponseDto currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new emma_media.video.exception.UnauthorizedException("User not authenticated");
        }

        List<Video> userVideo = videoRepository.findByUserId(currentUser.getId());
        return userVideo.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public VideoResponseDto updateVideo(Long id, String title, String description, MultipartFile file) {
        UserResponseDto currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new emma_media.video.exception.UnauthorizedException("User not authenticated");
        }

        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new emma_media.video.exception.ResourceNotFoundException("Video with ID " + id + " not found"));

        if (!video.getUser().getId().equals(currentUser.getId())) {
            throw new emma_media.music.exception.UnauthorizedException("You are not authorized to update this music");
        }

        if (title != null && !title.isEmpty()) {
            video.setTitle(title);
        }
        if (description != null && !description.isEmpty()) {
            video.setDescription(description);
        }

        if (file != null && !file.isEmpty()) {
            deleteFile(video.getFileUrl());
            String fileUrl = saveFile(file);
            video.setFileUrl(fileUrl);
        }

        Video updatedVideo = videoRepository.save(video);
        return mapToResponseDto(updatedVideo);
    }


    public VideoResponseDto getVideoById(Long id) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Video with ID " + id + " not found"));
        return mapToResponseDto(video);
    }

    public List<VideoResponseDto> getAllVideos() {
        return videoRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }


    public void deleteVideo(Long id, String userEmail) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Video with ID " + id + " not found"));

        if (!video.getUser().getEmail().equals(userEmail)) {
            throw new UnauthorizedException("You are not authorized to delete this video");
        }

        deleteFile(video.getFileUrl());

        videoRepository.delete(video);
    }

    private VideoResponseDto mapToResponseDto(Video video) {
        return VideoResponseDto.builder()
                .id(video.getId())
                .title(video.getTitle())
                .description(video.getDescription())
                .fileUrl(video.getFileUrl())
                .createdAt(video.getCreatedAt())
                .updatedAt(video.getUpdatedAt())
                .createdBy(video.getUser().getUsername())
                .build();
    }

    private String saveFile(MultipartFile file) {
        try {
            String uploadDir = "uploads/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("File upload failed", e);
        }
    }

    private void deleteFile(String filePath) {
        if (filePath != null && !filePath.isEmpty()) {
            try {
                Path path = Paths.get(filePath.replaceFirst("^/+", ""));
                if (Files.exists(path)) {
                    Files.delete(path);
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete file: " + filePath, e);
            }
        }
    }
}
