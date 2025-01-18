package com.example.Netflix.ApiUsers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ApiUserInitializer implements CommandLineRunner {
    @Autowired
    private ApiUserService apiUserService;

    @Value("${ADMIN_LOGIN}")
    private String adminLogin;

    @Value("${ADMIN_PASSWORD}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        apiUserService.initializeAdminUser(adminLogin, adminPassword);
    }
}

