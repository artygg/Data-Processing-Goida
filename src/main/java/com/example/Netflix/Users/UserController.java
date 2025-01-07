package com.example.Netflix.Users;

import com.example.Netflix.JSON.ResponseMessage;
import com.example.Netflix.JWT.JwtTokenFactory;
import com.example.Netflix.Warnings.Warning;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtTokenFactory jwtTokenFactory;

    @PostMapping("/registration")
    public ResponseEntity<?> registration(@RequestBody UserRequestBody userRequestBody) {
        String email = userRequestBody.getEmail();
        User user = new User();
        String jwt = jwtTokenFactory.generateToken(email);
        Warning warning = new Warning();

        if (userService.findUserByEmail(email).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Email is taken"));
        }

        user.setEmail(email);
        user.setWarning(warning);
        warning.setUser(user);
        user.setPassword(userRequestBody.getPassword());
        user.setToken(jwt);
        userService.saveUser(user);

        return ResponseEntity.ok(new ResponseMessage("User was registered successfully"));
    }

    @GetMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserRequestBody userRequestBody) {
        String email = userRequestBody.getEmail();

        Optional<User> optionalUser = userService.findUserByEmail(email);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            return ResponseEntity.ok(user);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Requested user was not found"));
    }

//    Leave this for SystemUser, will be transferred there later

//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody UserRequestBody userRequestBody) {
//        Optional<User> optionalUser = userService.findUserByEmail(userRequestBody.getEmail());
//        User user;
//
//        if (optionalUser.isPresent()) {
//            user = optionalUser.get();
//
//            if (!userService.isBanned(user)) {
//                try {
//                    SecurityContext contextHolder = SecurityContextHolder.createEmptyContext();
//
//                    Authentication authentication = authenticationManager.authenticate(
//                            new UsernamePasswordAuthenticationToken(
//                                    userRequestBody.getEmail(),
//                                    userRequestBody.getPassword()
//                            )
//                    );
//                    contextHolder.setAuthentication(authentication);
//                    SecurityContextHolder.setContext(contextHolder);
//                    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//                    String jwt = jwtTokenFactory.generateToken(userDetails.getUsername());
//
//                    user.setToken(jwt);
//                    userService.updateUser(user);
//
//                    return ResponseEntity.ok(user);
//                } catch (BadCredentialsException e) {
//                    user.getWarning().setLoginFaults(user.getWarning().getLoginFaults() + 1);
//
//                    if (user.getWarning().getLoginFaults() >= 3) {
//                        userService.banUser(user);
//                    }
//
//                    userService.updateUser(user);
//
//                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Wrong credentials"));
//                }
//            } else {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage("User is banned"));
//            }
//        }
//
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("User does not exist"));
//    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUserCredentials(@PathVariable Long id,
                                                   @RequestBody UserRequestBody userRequestBody) {
        Optional<User> optionalUser = userService.findUserByUserId(id);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (!userService.isBanned(user)) {
                user.setPassword(
                        userRequestBody.getPassword() != null ?
                                userRequestBody.getPassword() :
                                user.getPassword());
                user.setEmail(
                        userRequestBody.getEmail() != null ?
                                userRequestBody.getEmail() :
                                user.getEmail()
                );
                userService.saveUser(user);

                return ResponseEntity.ok(user);
            }

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage("User is banned"));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Requested user was not found"));
    }
}
