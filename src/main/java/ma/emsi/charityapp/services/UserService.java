package ma.emsi.charityapp.services;

import ma.emsi.charityapp.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();
    User createUser(User user);
    Optional<User> getUserById(int id);
    Optional<User> getUserByEmail(String email);
    User updateUser(int id, User userDetails);
    void deleteUser(int id);
    long countAllUsers();
    void toggleUserStatus(int id);
    void updateUserRole(int id, String role);
    Page<User> getAllUsers(Pageable pageable);
    boolean existsByEmail(String email);
    User saveUser(User user);
    long countActiveUsers();
}