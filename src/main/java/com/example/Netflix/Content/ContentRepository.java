package com.example.Netflix.Content;

import com.example.Netflix.enums.ContentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {
    List<Content> findAllByType(ContentType type);

    List<Content> findAllByTypeAndSeriesId(ContentType type, Integer seriesId);

    @Query("SELECT c FROM Content c JOIN c.genres g WHERE g.name = :genreName")
    List<Content> findAllByGenreName(@Param("genreName") String genreName);

    @Query("SELECT c FROM Content c JOIN c.resolutions r WHERE r.id = :resolutionId")
    List<Content> findAllByResolutionId(@Param("resolutionId") Long resolutionId);
}
