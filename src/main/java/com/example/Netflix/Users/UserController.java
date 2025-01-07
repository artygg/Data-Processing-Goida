package com.example.Netflix.Users;

import com.example.Netflix.JSON.ResponseMessage;
import com.example.Netflix.JWT.JwtTokenFactory;
import com.example.Netflix.Referals.Referral;
import com.example.Netflix.Referals.ReferralService;
import com.example.Netflix.Warnings.Warning;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtTokenFactory jwtTokenFactory;
    @Autowired
    private ReferralService referralService;

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

    @PostMapping("/invite/{id}")
    public ResponseEntity<?> inviteUserWithReferralLink(@PathVariable Long id,
                                                        @RequestBody User userBody) {
        Optional<User> optionalHostUser = userService.findUserByUserId(userBody.getId());
        Optional<User> optionalInvitedUser = userService.findUserByUserId(id);

        if (optionalHostUser.isPresent()) {
            User hostUser = optionalHostUser.get();

            if (optionalInvitedUser.isPresent()) {
                User invitedUser = optionalInvitedUser.get();
                Referral referral = new Referral();

                referral.setInvitedId(invitedUser.getId());
                referral.setHostId(hostUser.getId());

                referralService.saveReferral(referral);
                hostUser.setHasUsedReferralLink(true);
                userService.updateUser(hostUser);

                return ResponseEntity.ok("Referral was successfully saved");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Requested user to invite was not found");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Requested host was not found");
        }
    }
}
