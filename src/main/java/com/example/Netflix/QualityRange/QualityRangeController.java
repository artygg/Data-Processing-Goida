package com.example.Netflix.QualityRange;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/qualityRanges")
public class QualityRangeController {
    private final QualityRangeService qualityRangeService;

    public QualityRangeController(QualityRangeService qualityRangeService) {
        this.qualityRangeService = qualityRangeService;
    }

    @GetMapping("/content/{contentId}")
    public List<QualityRange> getQualityRangesByContentId(@PathVariable Long contentId) {
        return qualityRangeService.getQualityRangesByContentId(contentId);
    }

    @PostMapping
    public QualityRange createQualityRange(@RequestBody QualityRange qualityRange) {
        return qualityRangeService.createQualityRange(qualityRange);
    }
}
