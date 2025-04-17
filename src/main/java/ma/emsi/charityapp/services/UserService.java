package ma.emsi.charityapp.services;

import ma.emsi.charityapp.entities.User;
import ma.emsi.charityapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public Optional<User> getUserById(int id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User updateUser(int id, User userDetails) {
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setNom(userDetails.getNom());
            user.setEmail(userDetails.getEmail());
            user.setTelephone(userDetails.getTelephone());
            return userRepository.save(user);
        }
        return null;  // Ou gérer l'erreur de manière appropriée
    }

    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }
}
