package com.example.Netflix.Users;

import com.example.Netflix.JSON.ResponseMessage;
import com.example.Netflix.JWT.JwtTokenFactory;
import com.example.Netflix.Referals.Referral;
import com.example.Netflix.Referals.ReferralService;
import com.example.Netflix.Warnings.Warning;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

        User user = new User();
        String jwt = jwtTokenFactory.generateToken(email);
        Warning warning = new Warning();

        user.setEmail(email);
        user.setPassword(userRequestBody.getPassword());
        user.setToken(jwt);
        user.setWarning(warning);
        warning.setUser(user);

        userService.saveUser(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseMessage("User was registered successfully"));
    }

    @PostMapping(value = "/login",
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
        Optional<User> optionalUser = userService.findUserByUserId(id);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (userService.isBanned(user)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ResponseMessage("User is banned"));
            }

            user.setEmail(userRequestBody.getEmail() != null ? userRequestBody.getEmail() : user.getEmail());
            user.setPassword(userRequestBody.getPassword() != null ? userRequestBody.getPassword() : user.getPassword());

            userService.saveUser(user);
            return ResponseEntity.ok(user);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseMessage("Requested user was not found"));
    }

    @PostMapping(value = "/invite/{id}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> inviteUserWithReferralLink(@PathVariable UUID id,
                                                        @RequestBody @Valid UserInvitationDTO userBody) {
        Optional<User> optionalHostUser = userService.findUserByUserId(userBody.getHostID());
        Optional<User> optionalInvitedUser = userService.findUserByUserId(id);

        if (optionalHostUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage("Requested host was not found"));
        }

        User hostUser = optionalHostUser.get();

        if (optionalInvitedUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage("Requested user to invite was not found"));
        }

        if (hostUser.isHasUsedReferralLink()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Referral link had been used");
        }

        User invitedUser = optionalInvitedUser.get();
        Referral referral = new Referral();

        referral.setInvitedId(invitedUser.getId());
        referral.setHostId(hostUser.getId());

        referralService.saveReferral(referral);

        hostUser.setHasUsedReferralLink(true);
        userService.updateUser(hostUser);

        return ResponseEntity.ok(new ResponseMessage("Referral was successfully saved"));
    }
}