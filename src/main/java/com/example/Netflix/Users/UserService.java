package com.example.Netflix.Users;

import com.example.Netflix.JSON.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public void saveUser(User user) {
        String password = user.getPassword();
        user.setPassword(passwordEncoder.encode(password));

        userRepository.save(user);
    }

    public void registration(String email, String password, String token) {
        try {
            String hashedPassword = passwordEncoder.encode(password);

            userRepository.registerUser(email, hashedPassword, token);
        } catch (Exception e) {
            throw new RuntimeException("Failed registration of a user");
        }
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }

    public ResponseEntity<?> updateUserCredentials(UUID id, User userRequestBody) {
        try {
            userRepository.updateUserCredentials(id, userRequestBody.getEmail(), userRequestBody.getPassword());

            return ResponseEntity.ok(userRequestBody);
        } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ResponseMessage("Internal server error"));
        }
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    public Optional<User> findUserByUserId(UUID id) {
        return userRepository.findUserById(id);
    }

    public void banUser(User user) {
        user.setBanned(true);
        user.getWarning().setBanEndDate(LocalDateTime.now().plusMinutes(1));;
        userRepository.save(user);
    }

    public boolean isBanned(User user) {
        return user.isBanned();
    }

    public String generateToken() {
        return UUID.randomUUID().toString();
    }

    @Scheduled(fixedRate = 60000)
    public void unbanUser() {
        System.out.println("Checking unban");
        List<User> bannedUsers = userRepository.findAll().stream().filter(user -> user.isBanned() && user.getWarning().getBanEndDate() != null).toList();

        for (User user : bannedUsers) {
            if (LocalDateTime.now().isAfter(user.getWarning().getBanEndDate())) {
                user.setBanned(false);
                user.getWarning().setBanEndDate(null);
                user.getWarning().setLoginFaults(0);
                userRepository.save(user);
            }
        }
    }
}
