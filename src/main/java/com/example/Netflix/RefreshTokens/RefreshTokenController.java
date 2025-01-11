package com.example.Netflix.RefreshTokens;

import com.example.Netflix.ApiUsers.ApiUser;
import com.example.Netflix.ApiUsers.ApiUserService;
import com.example.Netflix.JWT.JwtTokenFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api-user/refresh-token")
public class RefreshTokenController {
    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private JwtTokenFactory jwtTokenFactory;
    @Autowired
    private ApiUserService apiUserService;

    @PostMapping(value = "/refresh",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> refreshJWTToken(@RequestBody RefreshTokenDTO refreshTokenDTO) {
        Optional<RefreshToken> optionalRefreshToken = refreshTokenService.findRefreshTokenByToken(refreshTokenDTO.getToken());

        if (optionalRefreshToken.isEmpty() || refreshTokenService.isRefreshedTokenExpired(optionalRefreshToken.get())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token was not found or expired");
        } else {
            RefreshToken refreshToken = optionalRefreshToken.get();
            String newJwt = jwtTokenFactory.generateToken(refreshToken.getUsername());
            Optional<ApiUser> optionalApiUser = apiUserService.findApiUserByLogin(refreshToken.getUsername());

            if (optionalApiUser.isPresent()) {
                ApiUser apiUser = optionalApiUser.get();

                apiUser.setToken(newJwt);
                apiUserService.updateSystemUser(apiUser);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Api user was not found");
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, "refreshToken=" + refreshToken + "; HttpOnly; Secure; Path=/api/auth/refresh; Max-Age=604800")
                    .body(Map.of("accessToken", newJwt));
        }
    }

}
