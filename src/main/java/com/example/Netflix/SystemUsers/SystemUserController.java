package com.example.Netflix.SystemUsers;

import com.example.Netflix.JWT.JwtTokenFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/system-user")
public class SystemUserController {
    @Autowired
    private SystemUserService systemUserService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenFactory jwtTokenFactory;

//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody SystemUser systemUserServiceBody) {
//        Optional<SystemUser> optionalUser = systemUserService.findSystemUserById(systemUserServiceBody.getId());
//        SystemUser user;
//
//        if (optionalUser.isPresent()) {
//            user = optionalUser.get();
//
//                try {
//                    SecurityContext contextHolder = SecurityContextHolder.createEmptyContext();
//
//                    Authentication authentication = authenticationManager.authenticate(
//                            new UsernamePasswordAuthenticationToken(
//                                    systemUserServiceBody.getLogin(),
//                                    systemUserServiceBody.getPassword()
//                            )
//                    );
//                    contextHolder.setAuthentication(authentication);
//                    SecurityContextHolder.setContext(contextHolder);
//                    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//                    String jwt = jwtTokenFactory.generateToken(userDetails.getUsername());
//
//                    user.setToken(jwt);
//                    SystemUserService.updateUser(user);
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
//        }
//
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("User does not exist"));
//    }
}
