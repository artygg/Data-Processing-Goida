package com.example.Netflix.Security;

import com.example.Netflix.ApiUsers.ApiUser;
import com.example.Netflix.ApiUsers.ApiUserService;
import com.example.Netflix.Users.User;
import com.example.Netflix.Users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class CustomDetailsService implements UserDetailsService {
    @Autowired
    private ApiUserService apiUserService;

    @Override
    public UserDetails loadUserByUsername(String login) {
        Optional<ApiUser> optionalUser = apiUserService.findApiUserByLogin(login);

        System.out.println("I am here");

        if (optionalUser.isPresent()) {
            ApiUser apiUser = optionalUser.get();
            System.out.println("I am here as well!: " + apiUser.getLogin());


            return new org.springframework.security.core.userdetails.User(apiUser.getLogin(), apiUser.getPassword(), new ArrayList<>());
        }

        throw new UsernameNotFoundException("User was not found");
    }
}
