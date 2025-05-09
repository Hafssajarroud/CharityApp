package ma.emsi.charityapp.services.impl;

import lombok.RequiredArgsConstructor;
import ma.emsi.charityapp.entities.User;
import ma.emsi.charityapp.entities.Role;
import ma.emsi.charityapp.repositories.UserRepository;
import ma.emsi.charityapp.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> getUserById(int id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public User updateUser(int id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        user.setNom(userDetails.getNom());
        user.setTelephone(userDetails.getTelephone());

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }

    @Override
    public long countAllUsers() {
        return userRepository.count();
    }

    @Override
    @Transactional
    public void toggleUserStatus(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateUserRole(int id, String role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        user.setRole(Role.valueOf(role));
        userRepository.save(user);
    }

    @Override
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public long countActiveUsers() {
        return userRepository.countByEnabledTrue();
    }
}