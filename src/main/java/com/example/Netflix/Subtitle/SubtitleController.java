package com.example.Netflix.Subtitle;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public ResponseEntity<?> createSubtitle(@RequestBody Subtitle subtitle) {
        try {
            ((org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

            return ResponseEntity.ok(subtitleService.createSubtitle(subtitle));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized request: " + e.getMessage());
        }
    }

    @DeleteMapping("/{contentId}/{language}")
    public ResponseEntity<?> deleteSubtitle(@PathVariable Long contentId, @PathVariable String language) {
        try {
            ((org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

            subtitleService.deleteSubtitle(contentId, language);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized request: " + e.getMessage());
        }
    }
}