package com.example.Netflix.Users;

import com.example.Netflix.JSON.ResponseMessage;
import com.example.Netflix.JWT.JwtTokenFactory;
import com.example.Netflix.Referals.ReferralService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtTokenFactory jwtTokenFactory;
    @Autowired
    private ReferralService referralService;

    @PostMapping(value = "/registration",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> registration(@RequestBody @Valid User userRequestBody) {
        String email = userRequestBody.getEmail();

        if (userService.findUserByEmail(email).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage("Email is already taken"));
        }

        String jwt = jwtTokenFactory.generateToken(email);

        userService.registration(email, userRequestBody.getPassword(), jwt);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseMessage("User was registered successfully"));
    }

    @GetMapping(value = "/login",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> login(@RequestBody @Valid User userRequestBody) {
        String email = userRequestBody.getEmail();

        Optional<User> optionalUser = userService.findUserByEmail(email);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            return ResponseEntity.ok(user);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseMessage("Requested user was not found"));
    }

    @PutMapping(value = "/{id}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> updateUserCredentials(@PathVariable UUID id,
                                                   @RequestBody @Valid User userRequestBody) {
        return userService.updateUserCredentials(id, userRequestBody);
    }

    @GetMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getAllUsers() {
        try {
            ((org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

            return ResponseEntity.ok(userService.findAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Error fetching users"));
        }
    }

    @PostMapping(value = "/invite/{id}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> inviteUserWithReferralLink(@PathVariable UUID id,
                                                        @RequestBody @Valid UserInvitationDTO userBody) {
        return referralService.createReferral(userBody.getHostID(), id);
    }

    @DeleteMapping(value = "/{id}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> deleteUserById(@PathVariable UUID id){
        try {
            ((org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
            userService.deleteById(id);

            return ResponseEntity.ok(new ResponseMessage("User deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessage("No authorization"));
        }
    }
}