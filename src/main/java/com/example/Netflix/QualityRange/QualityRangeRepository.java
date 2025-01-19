package com.example.Netflix.QualityRange;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QualityRangeRepository extends JpaRepository<QualityRange, QualityRangeId>
{
    List<QualityRange> findAllByContentId(Long contentId);
}