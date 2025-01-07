package com.example.Netflix.SystemUsers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SystemUserService {
    @Autowired
    private SystemUserRepository systemUserRepository;

    public Optional<SystemUser> findSystemUserById(Long id) {
        return systemUserRepository.findSystemUserById(id);
    }

    public void initializeAdminUser(String login,
                                    String password) {

    }
}
