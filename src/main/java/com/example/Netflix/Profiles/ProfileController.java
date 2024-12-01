package com.example.Netflix.Profiles;

import com.example.Netflix.Exceptions.ProfileLimitReached;
import com.example.Netflix.JSON.ResponseMessage;
import com.example.Netflix.Preferences.Preferences;
import com.example.Netflix.Preferences.PreferencesRequest;
import com.example.Netflix.Users.User;
import com.example.Netflix.Users.UserService;
import com.example.Netflix.enums.Classification;
import com.example.Netflix.enums.Genre;
import com.example.Netflix.enums.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/profiles")
public class ProfileController {
    @Autowired
    private ProfileService profileService;
    @Autowired
    private UserService userService;

    @PostMapping()
    public ResponseEntity<?> createProfile(@RequestParam() String username,
                                           @RequestParam() String imageUrl,
                                           @RequestParam(required = false) String age,
                                           @RequestParam(required = false) String language) throws ProfileLimitReached {
        String email;

        try {
            email = ((org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        } catch (ClassCastException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Requested user does not exist"));
        }

        Optional<User> optionalUser = userService.findUserByEmail(email);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (user.getProfiles().size() + 1 <= 4) {
                System.out.println("Size: " + user.getProfiles().size());
                Profile profile = new Profile();

                profile.setProfileName(username);
                profile.setProfilePhoto(imageUrl);
                profile.setUser(user);
                profile.setAge(age != null ? LocalDate.parse(age) : null);
                profile.setLanguage(language != null ? Language.valueOf(language.toUpperCase()) : Language.ENGLISH);

                user.addProfile(profile);
                userService.updateUser(user);

                return ResponseEntity.ok(profile);
            }

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage("You've reached the limit"));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Requested user was not found"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProfile(@PathVariable UUID id) {
        String email;

        try {
            email = ((org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        } catch (ClassCastException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Failed to authenticate"));
        }

        Optional<User> optionalUser = userService.findUserByEmail(email);
        Optional<Profile> optionalProfile = profileService.findProfileById(id);

        if (optionalUser.isPresent()) {
            if (optionalProfile.isPresent()) {
                Profile profile = optionalProfile.get();

                return ResponseEntity.ok(profile);
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Requested profile does not exist"));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Requested user does not exist");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProfile(@PathVariable UUID id,
                                           @RequestParam(required = false) String username,
                                           @RequestParam(required = false) String imageUrl,
                                           @RequestParam(required = false) String age,
                                           @RequestParam(required = false) String language) {
        try {
            ((org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        } catch (ClassCastException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Failed to authenticate"));
        }

        Optional<Profile> optionalProfile = profileService.findProfileById(id);

        if (optionalProfile.isPresent()) {
            Profile profile = optionalProfile.get();

            profile.setProfileName(username == null || username.isEmpty() ? profile.getProfileName() : username);
            profile.setProfilePhoto(imageUrl == null || imageUrl.isEmpty() ? profile.getProfilePhoto() : imageUrl);

            try {
                profile.setAge(age == null ? profile.getAge() : LocalDate.parse(age));
            } catch (DateTimeParseException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Invalid age format"));
            }

            try {
                profile.setLanguage(language == null ? profile.getLanguage() : Language.valueOf(language.toUpperCase()));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Invalid language value"));
            }

            profileService.saveProfile(profile);

            return ResponseEntity.ok(profile);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Requested profile was not found"));
    }

    @PutMapping("/{id}/preferences")
    public ResponseEntity<?> addPreferences(@PathVariable UUID id,
                                            @RequestBody PreferencesRequest preferencesRequest) {
        try {
            ((org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        } catch (ClassCastException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Failed to authenticate"));
        }
        System.out.println("IsInterested: " + preferencesRequest.isInterestedInFilms());

        Optional<Profile> optionalProfile = profileService.findProfileById(id);
        List<Genre> convertedGenres = new ArrayList<>();
        List<Classification> convertedClassifications = new ArrayList<>();

        if (preferencesRequest.getGenres() != null || !preferencesRequest.getGenres().isEmpty()) {

            for (String genre : preferencesRequest.getGenres()) {
                Genre convertedGenre = Genre.valueOf(genre.toUpperCase());
                convertedGenres.add(convertedGenre);
            }
        }
        if (preferencesRequest.getClassifications() != null || !preferencesRequest.getClassifications().isEmpty()) {
            for (String classification : preferencesRequest.getClassifications()) {
                Classification convertedClassification = Classification.valueOf(classification.toUpperCase());
                convertedClassifications.add(convertedClassification);
            }
        }

        if (optionalProfile.isPresent()) {
            Profile profile = optionalProfile.get();
            Preferences preferences = new Preferences();

            preferences.setClassifications(convertedClassifications);
            preferences.setGenres(convertedGenres);
            preferences.setInterestedInFilms(preferencesRequest.isInterestedInFilms());
            preferences.setInterestedInSeries(preferencesRequest.isInterestedInSeries());
            preferences.setInterestedInFilmsWithMinimumAge(preferencesRequest.isInterestedInFilmsWithMinimumAge());

            profile.setPreferences(preferences);
            profileService.saveProfile(profile);

            return ResponseEntity.ok(profile);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Requested profile was not found"));
    }
}
