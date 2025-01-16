package com.example.Netflix.Content;

import com.example.Netflix.enums.ContentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {
    List<Content> findAllByType(ContentType type);

    List<Content> findAllByTypeAndSeriesId(ContentType type, Integer seriesId);

    Optional<Content> findContentById(Long id);

    @Query("SELECT c FROM Content c JOIN c.genres g WHERE g.name = :genreName")
    List<Content> findAllByGenreName(@Param("genreName") String genreName);

    @Query("SELECT c FROM Content c JOIN c.resolutions r WHERE r.id = :resolutionId")
    List<Content> findAllByResolutionId(@Param("resolutionId") Long resolutionId);

    @Modifying
    @Transactional
    @Query(value = "CALL update_content_by_id(:id, :title, :description, :videoLink, :duration, :type, :season, :episodeNumber, :seriesId)", nativeQuery = true)
    void updateContentById(
            @org.springframework.data.repository.query.Param("id") Long id,
            @org.springframework.data.repository.query.Param("title") String title,
            @org.springframework.data.repository.query.Param("description") String description,
            @org.springframework.data.repository.query.Param("videoLink") String videoLink,
            @org.springframework.data.repository.query.Param("duration") Double duration,
            @org.springframework.data.repository.query.Param("type") String type,
            @org.springframework.data.repository.query.Param("season") Integer season,
            @org.springframework.data.repository.query.Param("episodeNumber") Integer episodeNumber,
            @org.springframework.data.repository.query.Param("seriesId") Integer seriesId
    );

    boolean existsById(Long id);
}
