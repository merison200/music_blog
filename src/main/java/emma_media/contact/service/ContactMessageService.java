package emma_media.contact.service;

import emma_media.contact.dto.ContactMessageDTO;
import emma_media.contact.entity.ContactMessage;
import emma_media.contact.repository.ContactMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContactMessageService {

    @Autowired
    private ContactMessageRepository contactMessageRepository;


    public ContactMessage saveMessage(ContactMessageDTO dto) {
        if (contactMessageRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("This email has already been used to send a message.");
        }

        ContactMessage message = new ContactMessage();
        message.setName(dto.getName());
        message.setPhone(dto.getPhone());
        message.setEmail(dto.getEmail());
        message.setMessage(dto.getMessage());
        return contactMessageRepository.save(message);
    }

    public boolean emailExists(String email) {
        return contactMessageRepository.existsByEmail(email);
    }
}