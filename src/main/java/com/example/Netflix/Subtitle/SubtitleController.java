package com.example.Netflix.Subtitle;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subtitles")
public class SubtitleController {
    private final SubtitleService subtitleService;

    public SubtitleController(SubtitleService subtitleService) {
        this.subtitleService = subtitleService;
    }

    @GetMapping("/{contentId}")
    public List<Subtitle> getSubtitlesByContentId(@PathVariable Long contentId) {
        return subtitleService.getSubtitlesByContentId(contentId);
    }

    @PostMapping
    public Subtitle createSubtitle(@RequestBody Subtitle subtitle) {
        return subtitleService.createSubtitle(subtitle);
    }

    @DeleteMapping("/{contentId}/{language}")
    public ResponseEntity<Void> deleteSubtitle(@PathVariable Long contentId, @PathVariable String language) {
        subtitleService.deleteSubtitle(contentId, language);
        return ResponseEntity.noContent().build();
    }
}