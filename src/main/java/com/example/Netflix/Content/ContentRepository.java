package com.example.Netflix.Content;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContentRepository extends JpaRepository<Content, Integer>
{
    Optional<Content> findContentById(Long id);
}
