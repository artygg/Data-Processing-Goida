package com.example.Netflix.Users;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
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
//    @Autowired
//    private JavaMailSender javaMailSender;

    public void saveUser(User user) {
        String password = user.getPassword();
        user.setPassword(passwordEncoder.encode(password));

        userRepository.save(user);
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    public void banUser(User user) {
        user.setBanned(true);
        user.setBanUntil(LocalDateTime.now().plusMinutes(1));
        userRepository.save(user);
    }

    public boolean isBanned(User user) {
        return user.isBanned();
    }

    public String generateToken() {
        return UUID.randomUUID().toString();
    }

    public void sendEmail(User user, String token) {
        String subject = "Email verification";
        String verificationUrl = "http://localhost:3000/verification?token=" + token;
        String message = "Please verify your email by clicking that link: " + verificationUrl;

//        SimpleMailMessage mailMessage = new SimpleMailMessage();
//        mailMessage.setTo(user.getEmail());
//        mailMessage.setSubject(subject);
//        mailMessage.setText(message);
//        javaMailSender.send(mailMessage);
    }

    public Optional<User> findUserByToken(String token) {
        return userRepository.findUserByToken(token);
    }

    @Scheduled(fixedRate = 6000)
    public void unbanUser() {
        System.out.println("Checking unban");
        List<User> bannedUsers = userRepository.findAll().stream().filter(user -> user.isBanned() && user.getBanUntil() != null).toList();

        for (User user : bannedUsers) {
            if (LocalDateTime.now().isAfter(user.getBanUntil())) {
                user.setBanned(false);
                user.setBanUntil(null);
                user.setLoginAttempt(0);
                userRepository.save(user);
            }
        }
    }
}
