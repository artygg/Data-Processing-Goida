package com.example.Netflix.Subtitle;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubtitleRepository extends JpaRepository<Subtitle, SubtitleId> {
    List<Subtitle> findAllByContentId(Long contentId);
}
