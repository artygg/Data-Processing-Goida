package com.example.Netflix.ApiUsers;

import com.example.Netflix.JSON.ResponseMessage;
import com.example.Netflix.JWT.JwtTokenFactory;
import com.example.Netflix.RefreshTokens.RefreshToken;
import com.example.Netflix.RefreshTokens.RefreshTokenDTO;
import com.example.Netflix.RefreshTokens.RefreshTokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
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

import java.util.Map;
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

    @Autowired
    private RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid ApiUser apiUserServiceBody) {
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
                    RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());

                    user.setToken(jwt);
                    apiUserService.updateSystemUser(user);

                    return ResponseEntity.ok()
                            .header(HttpHeaders.SET_COOKIE, "refreshToken=" + refreshToken.getToken() + "; HttpOnly; Secure; Path=/api/auth/refresh; Max-Age=604800")
                            .body(Map.of("token: ", user.getToken()));
                } catch (BadCredentialsException e) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Wrong credentials"));
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
                }
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("User does not exist"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody @Valid RefreshTokenDTO refreshTokenDTO) {
        Optional<RefreshToken> optionalRefreshToken = refreshTokenService.findRefreshTokenByToken(refreshTokenDTO.getToken());

        if (optionalRefreshToken.isPresent()) {
            RefreshToken refreshToken = optionalRefreshToken.get();

            Optional<ApiUser> optionalApiUser = apiUserService.findApiUserByLogin(refreshToken.getUsername());

            if (optionalApiUser.isPresent()) {
                ApiUser apiUser = optionalApiUser.get();

                apiUser.setToken(null);
                refreshTokenService.deleteRefreshTokenByToken(refreshTokenDTO.getToken());
                apiUserService.updateSystemUser(apiUser);

                return ResponseEntity.ok("Successfully logged out");
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized request");
    }
}
