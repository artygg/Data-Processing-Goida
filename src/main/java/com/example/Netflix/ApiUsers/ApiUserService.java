package com.example.Netflix.ApiUsers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ApiUserService {
    @Autowired
    private ApiUserRepository apiUserRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<ApiUser> findApiUserById(Long id) {
        return apiUserRepository.findSystemUserById(id);
    }

    public Optional<ApiUser> findApiUserByLogin(String login) {
        return apiUserRepository.findSystemUserByLogin(login);
    }

    @Transactional
    public void initializeAdminUser(String login,
                                    String password) {
        Optional<ApiUser> optionalSystemUser = apiUserRepository.findSystemUserByLogin(login);

        if (login.isEmpty() || password.isEmpty()) {
            return;
        }

        if (optionalSystemUser.isEmpty()) {
            ApiUser apiUser = new ApiUser();

            apiUser.setLogin(login);
            apiUser.setPassword(passwordEncoder.encode(password));

            apiUserRepository.save(apiUser);
        }
    }

    public void updateSystemUser(ApiUser apiUser) {
        apiUserRepository.save(apiUser);
    }
}
