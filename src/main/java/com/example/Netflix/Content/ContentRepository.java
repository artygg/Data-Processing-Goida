package com.example.Netflix.Content;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {
    @Query("SELECT c FROM Content c JOIN c.genres g WHERE g.name = :genreName")
    List<Content> findAllByGenreName(@Param("genreName") String genreName);
}