package emma_media.contact.controller;

import emma_media.contact.dto.ContactMessageDTO;
import emma_media.contact.repository.ContactMessageRepository;
import emma_media.contact.service.ContactMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact")
public class ContactMessageController {

    @Autowired
    private ContactMessageService contactMessageService;

    @Autowired
    private ContactMessageRepository contactMessageRepository;

    @PostMapping("/submit")
    public ResponseEntity<?> submitMessage(@RequestBody ContactMessageDTO dto) {
        try {
            return ResponseEntity.ok(contactMessageService.saveMessage(dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/check-email")
    public ResponseEntity<String> checkEmail(@RequestParam String email) {
        if (contactMessageService.emailExists(email)) {
            return ResponseEntity.badRequest().body("Email already exists.");
        }
        return ResponseEntity.ok("Email does not exist.");
    }
}