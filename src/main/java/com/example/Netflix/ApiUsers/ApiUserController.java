package com.example.Netflix.ApiUsers;

import com.example.Netflix.JSON.ResponseMessage;
import com.example.Netflix.JWT.JwtTokenFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api-user")
public class ApiUserController {
    @Autowired
    private ApiUserService apiUserService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenFactory jwtTokenFactory;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody ApiUser apiUserServiceBody) {
        Optional<ApiUser> optionalUser = apiUserService.findApiUserByLogin(apiUserServiceBody.getLogin());
        ApiUser user;

        if (optionalUser.isPresent()) {
            user = optionalUser.get();

                try {
                    SecurityContext contextHolder = SecurityContextHolder.createEmptyContext();

                    Authentication authentication = authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    apiUserServiceBody.getLogin(),
                                    apiUserServiceBody.getPassword()
                            )
                    );
                    contextHolder.setAuthentication(authentication);
                    SecurityContextHolder.setContext(contextHolder);
                    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                    String jwt = jwtTokenFactory.generateToken(userDetails.getUsername());

                    user.setToken(jwt);
                    apiUserService.updateSystemUser(user);

                    return ResponseEntity.ok(user);
                } catch (BadCredentialsException e) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Wrong credentials"));
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
                }
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("User does not exist"));
    }
}
