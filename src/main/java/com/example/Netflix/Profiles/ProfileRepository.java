package com.example.Netflix.Profiles;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, UUID> {
    Optional<Profile> findProfileById(UUID id);

    @Modifying
    @Transactional
    @Query(value = "CALL update_profile_preferences(:profileId, :classifications, :genres, :interestedInFilms, :interestedInSeries, :interestedInFilmsWithMinAge)", nativeQuery = true)
    void updateProfilePreferences(
            @Param("profileId") UUID profileId,
            @Param("classifications") String[] classifications,
            @Param("genres") String[] genres,
            @Param("interestedInFilms") Boolean interestedInFilms,
            @Param("interestedInSeries") Boolean interestedInSeries,
            @Param("interestedInFilmsWithMinAge") Boolean interestedInFilmsWithMinAge
    );

    @Modifying
    @Transactional
    @Query(value = "CALL remove_from_watch_later(:profileId, :contentId)", nativeQuery = true)
    void removeFromWatchLater(
            @Param("profileId") UUID profileId,
            @Param("contentId") Long contentId
    );

    @Modifying
    @Transactional
    @Query(value = "CALL update_profile(:profileId, :profileName, :profilePhoto, CAST(:age AS DATE), :language)", nativeQuery = true)
    void updateProfile(
            @Param("profileId") UUID profileId,
            @Param("profileName") String profileName,
            @Param("profilePhoto") String profilePhoto,
            @Param("age") String age,
            @Param("language") String language
    );

    @Modifying
    @Transactional
    @Query(value = "CALL create_profile(:userId, :profileName, :profilePhoto, :age, :language)", nativeQuery = true)
    void createProfile(
            @Param("userId") UUID userId,
            @Param("profileName") String profileName,
            @Param("profilePhoto") String profilePhoto,
            @Param("age") Date age,
            @Param("language") String language
    );
}
