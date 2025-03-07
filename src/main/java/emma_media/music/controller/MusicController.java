package emma_media.music.controller;

import emma_media.music.dto.MusicResponseDto;
import emma_media.music.service.MusicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/music")
@RequiredArgsConstructor
public class MusicController {

    private final MusicService musicService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MusicResponseDto> createMusic(
            @RequestParam("title") String title,
            @RequestParam("artist") String artist,
            @RequestParam("file") MultipartFile file,
            Principal principal) {

        String userEmail = principal.getName();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(musicService.createMusic(title, artist, file, userEmail));
    }

    @GetMapping("/my-music")
    public ResponseEntity<List<MusicResponseDto>> getMusicByUser() {
        List<MusicResponseDto> musicList = musicService.getMusicByUser();
        return ResponseEntity.ok(musicList);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MusicResponseDto> updateMusic(
            @PathVariable Long id,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "artist", required = false) String artist,
            @RequestParam(value = "file", required = false) MultipartFile file) {

        MusicResponseDto updatedMusic = musicService.updateMusic(id, title, artist, file);
        return ResponseEntity.ok(updatedMusic);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MusicResponseDto> getMusicById(@PathVariable Long id) {
        return ResponseEntity.ok(musicService.getMusicById(id));
    }

    @GetMapping
    public ResponseEntity<List<MusicResponseDto>> getAllMusic() {
        return ResponseEntity.ok(musicService.getAllMusic());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMusic(@PathVariable Long id, Principal principal) {
        String userEmail = principal.getName();
        musicService.deleteMusic(id, userEmail);
        return ResponseEntity.noContent().build();
    }
}
