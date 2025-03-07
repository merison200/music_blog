package emma_media.contact.repository;

import emma_media.contact.entity.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {

    boolean existsByEmail(String email);
}