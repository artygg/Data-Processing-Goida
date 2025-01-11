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
import org.springframework.http.MediaType;
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

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> createProfile(@RequestBody ProfileDTO profileBody) throws ProfileLimitReached {
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
                Profile profile = new Profile();

                profile.setProfileName(profileBody.getProfileName());
                profile.setProfilePhoto(profileBody.getProfilePhoto());
                profile.setUser(user);
                profile.setAge(profileBody.getAge() != null ? LocalDate.parse(profileBody.getAge()) : null);
                profile.setLanguage(profileBody.getLanguage() != null ? Language.valueOf(profileBody.getLanguage().toUpperCase()) : Language.ENGLISH);

                user.addProfile(profile);
                userService.updateUser(user);

                return ResponseEntity.ok(profile);
            }

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage("You've reached the limit"));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Requested user was not found"));
    }

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
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

    @PutMapping(value = "/{id}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> updateProfile(@PathVariable UUID id,
                                           @RequestBody ProfileDTO profileBody) {
        try {
            ((org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        } catch (ClassCastException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Failed to authenticate"));
        }

        Optional<Profile> optionalProfile = profileService.findProfileById(id);

        if (optionalProfile.isPresent()) {
            Profile profile = optionalProfile.get();

            profile.setProfileName(profileBody.getProfileName() == null || profileBody.getProfileName().isEmpty() ? profile.getProfileName() : profileBody.getProfileName());
            profile.setProfilePhoto(profileBody.getProfilePhoto() == null || profileBody.getProfilePhoto().isEmpty() ? profile.getProfilePhoto() : profileBody.getProfilePhoto());

            try {
                profile.setAge(profileBody.getAge() == null ? profile.getAge() : LocalDate.parse(profileBody.getAge()));
            } catch (DateTimeParseException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Invalid age format"));
            }

            try {
                profile.setLanguage(profileBody.getLanguage() == null ? profile.getLanguage() : Language.valueOf(profileBody.getLanguage().toUpperCase()));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Invalid language value"));
            }

            profileService.saveProfile(profile);

            return ResponseEntity.ok(profile);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Requested profile was not found"));
    }

    @PutMapping(value = "/{id}/preferences",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> addPreferences(@PathVariable UUID id,
                                            @RequestBody PreferencesRequest preferencesRequest) {
        try {
            ((org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        } catch (ClassCastException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Failed to authenticate"));
        }

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
            preferences.setProfile(profile);

            profile.setPreferences(preferences);
            profileService.saveProfile(profile);

            return ResponseEntity.ok(profile);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Requested profile was not found"));
    }
}
