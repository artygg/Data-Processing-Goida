package com.example.Netflix.Profiles;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, UUID> {
    Optional<Profile> findProfileById(UUID id);

    @Query(value = "CALL update_profile_preferences(:profileId, :classifications, :genres, :interestedInFilms, :interestedInSeries, :interestedInFilmsWithMinAge)", nativeQuery = true)
    void updateProfilePreferences(
            @Param("profileId") UUID profileId,
            @Param("classifications") String[] classifications,
            @Param("genres") String[] genres,
            @Param("interestedInFilms") Boolean interestedInFilms,
            @Param("interestedInSeries") Boolean interestedInSeries,
            @Param("interestedInFilmsWithMinAge") Boolean interestedInFilmsWithMinAge
    );

    @Query(value = "CALL remove_from_watch_later(:profileId, :contentId)", nativeQuery = true)
    void removeFromWatchLater(
            @Param("profileId") UUID profileId,
            @Param("contentId") Long contentId
    );

    @Query(value = "CALL update_profile(:profileId, :profileName, :profilePhoto, :age, :language)", nativeQuery = true)
    void updateProfile(
            @Param("profileId") UUID profileId,
            @Param("profileName") String profileName,
            @Param("profilePhoto") String profilePhoto,
            @Param("age") String age,
            @Param("language") String language
    );

    @Query(value = "CALL create_profile(:userId, :profileName, :profilePhoto, :age, :language)", nativeQuery = true)
    void createProfile(
            @Param("userId") UUID userId,
            @Param("profileName") String profileName,
            @Param("profilePhoto") String profilePhoto,
            @Param("age") String age, // Pass in ISO format date (e.g., "2000-01-01")
            @Param("language") String language
    );
}
