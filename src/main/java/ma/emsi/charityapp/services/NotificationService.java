package ma.emsi.charityapp.services;

import ma.emsi.charityapp.entities.Notification;
import ma.emsi.charityapp.repositories.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public Notification createNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    public Optional<Notification> getNotificationById(int id) {
        return notificationRepository.findById(id);
    }

    public void deleteNotification(int id) {
        notificationRepository.deleteById(id);
    }
}
