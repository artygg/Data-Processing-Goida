package com.example.Netflix.QualityRange;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QualityRangeService {
    private final QualityRangeRepository qualityRangeRepository;

    public QualityRangeService(QualityRangeRepository qualityRangeRepository) {
        this.qualityRangeRepository = qualityRangeRepository;
    }

    public List<QualityRange> getQualityRangesByContentId(Long contentId) {
        return qualityRangeRepository.findAllByContentId(contentId);
    }

    public QualityRange createQualityRange(QualityRange qualityRange) {
        return qualityRangeRepository.save(qualityRange);
    }

}