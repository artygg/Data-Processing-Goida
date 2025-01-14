package com.example.Netflix.Profiles;

import com.example.Netflix.Preferences.PreferencesRequest;
import com.example.Netflix.Users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ProfileService {
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private UserService userService;

    public void saveProfile(Profile profile) {
        profileRepository.save(profile);
    }

    public Optional<Profile> findProfileById(UUID id) {
        return profileRepository.findProfileById(id);
    }

    public ResponseEntity<?> updatePreferences(UUID profileId, PreferencesRequest preferencesRequest) {
        try {
            profileRepository.updateProfilePreferences(
                    profileId,
                    preferencesRequest.getClassifications().toArray(new String[0]),
                    preferencesRequest.getGenres().toArray(new String[0]),
                    preferencesRequest.isInterestedInFilms(),
                    preferencesRequest.isInterestedInSeries(),
                    preferencesRequest.isInterestedInFilmsWithMinimumAge()
            );

            return ResponseEntity.ok(profileRepository.findProfileById(profileId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    public ResponseEntity<?> removeFromWatchLater(UUID profileId, Long contentId) {
        try {
            profileRepository.removeFromWatchLater(profileId, contentId);

            return ResponseEntity.ok(profileRepository.findProfileById(profileId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    public ResponseEntity<?> updateProfile(UUID profileId, String profileName, String profilePhoto, String age, String language) {
        try {
            profileRepository.updateProfile(profileId, profileName, profilePhoto, age, language);

            return ResponseEntity.ok(profileRepository.findProfileById(profileId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    public ResponseEntity<?> createProfile(UUID userId, String profileName, String profilePhoto, String age, String language) {
        try {
            profileRepository.createProfile(userId, profileName, profilePhoto, age, language);

            return ResponseEntity.ok(userService.findUserByUserId(userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
