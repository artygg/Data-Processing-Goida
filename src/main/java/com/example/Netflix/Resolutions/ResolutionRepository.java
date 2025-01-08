package com.example.Netflix.Resolutions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResolutionRepository extends JpaRepository<Resolution, Integer> {
    Optional<Resolution> findResolutionById(int id);
    void deleteResolutionById(int id);
}
