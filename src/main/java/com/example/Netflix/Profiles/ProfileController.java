package com.example.Netflix.Profiles;

import com.example.Netflix.Content.Content;
import com.example.Netflix.Content.ContentService;
import com.example.Netflix.Exceptions.ProfileLimitReached;
import com.example.Netflix.Genre.Genre;
import com.example.Netflix.JSON.ResponseMessage;
import com.example.Netflix.Preferences.Preferences;
import com.example.Netflix.Preferences.PreferencesRequest;
import com.example.Netflix.Users.User;
import com.example.Netflix.Users.UserService;
import com.example.Netflix.enums.Classification;
import com.example.Netflix.enums.Language;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

@RestController
@RequestMapping("/profiles")
public class ProfileController {
    @Autowired
    private ProfileService profileService;
    @Autowired
    private UserService userService;
    @Autowired
    private ContentService contentService;

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> createProfile(@RequestBody @Valid ProfileDTO profileBody) throws ProfileLimitReached {
        try {
            System.out.println("Context: " + SecurityContextHolder.getContext().getAuthentication());
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            System.out.println("System user: " + username);
            if (username == null || username.isEmpty()) {
                throw new ClassCastException();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessage("Unauthorized request"));
        }

        Optional<User> optionalUser = userService.findUserByUserId(profileBody.getUserID());

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Requested user was not found"));
        }

        User user = optionalUser.get();
        if (user.getProfiles().size() >= 4) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage("You've reached the limit"));
        }

        Profile profile = new Profile();
        profile.setProfileName(profileBody.getProfileName());
        profile.setProfilePhoto(profileBody.getProfilePhoto());
        profile.setUser(user);

        if (profileBody.getAge() != null) {
            try {
                profile.setAge(LocalDate.parse(profileBody.getAge()));
            } catch (DateTimeParseException e) {
                return ResponseEntity.badRequest().body(new ResponseMessage("Invalid date format"));
            }
        }

        profile.setLanguage(
                profileBody.getLanguage() != null
                        ? Language.valueOf(profileBody.getLanguage().toUpperCase())
                        : Language.ENGLISH
        );

        user.addProfile(profile);
        userService.updateUser(user);
        return ResponseEntity.ok(profile);
    }

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getProfile(@PathVariable UUID id) {
        try {
            ((org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessage("Failed to authenticate"));
        }

        Optional<Profile> optionalProfile = profileService.findProfileById(id);

        if (optionalProfile.isPresent()) {
            Profile profile = optionalProfile.get();

            return ResponseEntity.ok(profile);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Requested profile does not exist");
    }

    @PutMapping(value = "/{id}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> updateProfile(@PathVariable UUID id,
                                           @RequestBody UpdateProfileDTO profileBody) {
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
                if (profileBody.getAge() != null) {
                    profile.setAge(LocalDate.parse(profileBody.getAge()));
                } else if (profile.getAge() != null) {
                    profile.setAge(profile.getAge());
                }
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
        System.out.println("Pref req" + preferencesRequest.isInterestedInFilms());
        try {
            ((org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        } catch (ClassCastException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessage("Failed to authenticate"));
        }

        Optional<Profile> optionalProfile = profileService.findProfileById(id);
        List<Classification> convertedClassifications = new ArrayList<>();

        if (preferencesRequest.getClassifications() != null) {
            if ( !preferencesRequest.getClassifications().isEmpty()) {
                for (String classification : preferencesRequest.getClassifications()) {
                    Classification convertedClassification = Classification.valueOf(classification.toUpperCase());
                    convertedClassifications.add(convertedClassification);
                }
            }
        }

        if (optionalProfile.isPresent()) {
            Profile profile = optionalProfile.get();
            Preferences preferences = new Preferences();

            preferences.setClassifications(convertedClassifications);
            preferences.setGenres(preferencesRequest.getGenres());
            preferences.setInterestedInFilms(preferencesRequest.isInterestedInFilms());
            System.out.println(preferencesRequest.isInterestedInSeries());
            preferences.setInterestedInSeries(preferencesRequest.isInterestedInSeries());
            preferences.setInterestedInFilmsWithMinimumAge(preferencesRequest.isInterestedInFilmsWithMinimumAge());
            preferences.setProfile(profile);

            profile.setPreferences(preferences);
            profileService.saveProfile(profile);

            return ResponseEntity.ok(profile);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Requested profile was not found"));
    }

    @GetMapping("/{id}/watch-later")
    public ResponseEntity<?> getAllWatchLater(@PathVariable UUID id) {
        Optional<Profile> optionalProfile = profileService.findProfileById(id);

        if (optionalProfile.isPresent()) {
            Profile profile = optionalProfile.get();

            return ResponseEntity.ok(profile.getWatchLater());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Profile was not found");
    }

    @PutMapping("/{id}/watch-later")
    public ResponseEntity<?> addWatchLaterToProfile(@PathVariable UUID id,
                                                   @RequestBody @Valid Content content) {
        Optional<Profile> optionalProfile = profileService.findProfileById(id);

        if (optionalProfile.isPresent()) {
            Profile profile = optionalProfile.get();
            profile.addWatchLater(content);

            return ResponseEntity.ok("Successfully added to watch later list");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Profile was not found");
    }

    @DeleteMapping("/{id}/watch-later/{contentId}")
    public ResponseEntity<?> deleteFromWatchList(@PathVariable UUID id,
                                                 @PathVariable Long contentId) {
        Optional<Profile> optionalProfile = profileService.findProfileById(id);
        Optional<Content> optionalContent = contentService.findById(contentId);

        if (optionalProfile.isPresent()) {
            Profile profile = optionalProfile.get();

            if (optionalContent.isPresent()) {
                Content content = optionalContent.get();
                List<Content> watchLater = profile.getWatchLater();

                Iterator<Content> iterator = watchLater.iterator();

                while (iterator.hasNext()) {
                    if (iterator.next().getId().equals(content.getId())) {
                        iterator.remove();
                    }
                }

                profile.setWatchLater(watchLater);

                return ResponseEntity.ok("Successfully removed from watch later list: " + watchLater);
            }

            return ResponseEntity.status(HttpStatus.CONFLICT).body("Could not remove an element");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Profile was not found");
    }
}
