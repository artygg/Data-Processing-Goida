package com.example.Netflix.Profiles;

import com.example.Netflix.Preferences.PreferencesRequest;
import com.example.Netflix.Users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.Arrays;
import java.util.Objects;
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
            String[] classifications = preferencesRequest.getClassifications() != null
                    ? preferencesRequest.getClassifications()
                    .stream()
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .toArray(String[]::new)
                    : null;

            String[] genres = preferencesRequest.getGenres()
                    .stream()
                    .filter(Objects::nonNull)
                    .map(genre -> String.format("{\"id\": %d, \"name\": \"%s\"}", genre.getId(), genre.getName()))  // Creating JSON format strings
                    .toArray(String[]::new);

            System.out.println("Genres: " + Arrays.toString(genres));

            profileRepository.updateProfilePreferences(
                    profileId,
                    classifications,
                    genres,
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
            profileRepository.createProfile(userId, profileName, profilePhoto, age != null ? Date.valueOf(age) : null, language);

            return ResponseEntity.ok(userService.findUserByUserId(userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
