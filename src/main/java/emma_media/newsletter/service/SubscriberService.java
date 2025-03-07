package emma_media.newsletter.service;

import emma_media.newsletter.entity.Subscriber;
import emma_media.newsletter.repository.SubscriberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubscriberService {

    @Autowired
    private SubscriberRepository subscriberRepository;

    public String subscribe(String email) {
        if (subscriberRepository.findByEmail(email).isPresent()) {
            return "Email is already subscribed!";
        }
        subscriberRepository.save(new Subscriber(null, email));
        return "Subscription successful!";
    }
}