package com.example.Netflix.Profiles;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ProfileService {
    @Autowired
    private ProfileRepository profileRepository;

    public void saveProfile(Profile profile) {
        profileRepository.save(profile);
    }

    public Optional<Profile> findProfileById(UUID id) {
        return profileRepository.findProfileById(id);
    }
}
