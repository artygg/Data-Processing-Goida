package com.example.Netflix.Resolutions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface ResolutionRepository extends JpaRepository<Resolution, Integer> {
    void deleteResolutionById(int id);

    @Transactional
    @Query("SELECT r FROM Resolution r WHERE r.id = :resolutionId")
    Optional<Resolution> findResolutionById(@Param("resolutionId") Integer resolutionId);
}
