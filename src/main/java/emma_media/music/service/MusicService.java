package emma_media.music.service;

import emma_media.music.dto.MusicResponseDto;
import emma_media.music.entity.Music;
import emma_media.music.exception.ResourceNotFoundException;
import emma_media.music.exception.UnauthorizedException;
import emma_media.music.exception.UserNotFoundException;
import emma_media.music.repository.MusicRepository;
import emma_media.user.dto.UserResponseDto;
import emma_media.user.entity.User;
import emma_media.user.repository.UserRepository;
import emma_media.user.service.UserService;
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
public class MusicService {

    @Autowired
    private MusicRepository musicRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    public MusicResponseDto createMusic(String title, String artist, MultipartFile file, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String fileUrl = saveFile(file);

        Music music = new Music();
        music.setTitle(title);
        music.setArtist(artist);
        music.setFileUrl(fileUrl);
        music.setUser(user);

        Music savedMusic = musicRepository.save(music);
        return mapToResponseDto(savedMusic);
    }

    public List<MusicResponseDto> getMusicByUser() {
        UserResponseDto currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new UnauthorizedException("User not authenticated");
        }

        List<Music> userMusic = musicRepository.findByUserId(currentUser.getId());
        return userMusic.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public MusicResponseDto updateMusic(Long id, String title, String artist, MultipartFile file) {
        UserResponseDto currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new UnauthorizedException("User not authenticated");
        }

        Music music = musicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Music with ID " + id + " not found"));

        if (!music.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You are not authorized to update this music");
        }

        if (title != null && !title.isEmpty()) {
            music.setTitle(title);
        }
        if (artist != null && !artist.isEmpty()) {
            music.setArtist(artist);
        }

        if (file != null && !file.isEmpty()) {
            deleteFile(music.getFileUrl());
            String fileUrl = saveFile(file);
            music.setFileUrl(fileUrl);
        }

        Music updatedMusic = musicRepository.save(music);
        return mapToResponseDto(updatedMusic);
    }

    public void deleteMusic(Long id, String userEmail) {
        Music music = musicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Music with ID " + id + " not found"));

        if (!music.getUser().getEmail().equals(userEmail)) {
            throw new UnauthorizedException("You are not authorized to delete this music");
        }

        deleteFile(music.getFileUrl());

        musicRepository.delete(music);
    }

    public MusicResponseDto getMusicById(Long id) {
        Music music = musicRepository.findById(id)
                .orElseThrow(() -> new emma_media.music.exception.ResourceNotFoundException("Music with ID " + id + " not found"));
        return mapToResponseDto(music);
    }


    public List<MusicResponseDto> getAllMusic() {
        return musicRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    private MusicResponseDto mapToResponseDto(Music music) {
        return MusicResponseDto.builder()
                .id(music.getId())
                .title(music.getTitle())
                .artist(music.getArtist())
                .fileUrl(music.getFileUrl())
                .createdAt(music.getCreatedAt())
                .updatedAt(music.getUpdatedAt())
                .createdBy(music.getUser().getUsername())
                .build();
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
}
