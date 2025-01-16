package com.example.Netflix.Profiles;

import com.example.Netflix.Content.Content;
import com.example.Netflix.Content.ContentRepository;
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
import org.springframework.security.core.Transient;
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
    private ContentService contentService;
    @Autowired
    private ContentRepository contentRepository;

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

        return profileService.createProfile(profileBody.getUserID(), profileBody.getProfileName(), profileBody.getProfilePhoto(), profileBody.getAge(), profileBody.getLanguage());
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

        return profileService.updateProfile(id, profileBody.getProfileName(), profileBody.getProfilePhoto(), profileBody.getAge(), profileBody.getLanguage());
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

        return profileService.updatePreferences(id, preferencesRequest);
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


        try {if (optionalProfile.isPresent()) {
            Profile profile = optionalProfile.get();
            profile.addWatchLater(content);

            if (content.getId() == null || !contentRepository.existsById(content.getId())) {
                contentService.saveContent(content);
            }

            profileService.saveProfile(profile);

            return ResponseEntity.ok("Successfully added to watch later list");
        }} catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Profile was not found");
    }

    @DeleteMapping("/{id}/watch-later/{contentId}")
    public ResponseEntity<?> deleteFromWatchList(@PathVariable UUID id,
                                                 @PathVariable Long contentId) {
        return profileService.removeFromWatchLater(id, contentId);
    }
}
