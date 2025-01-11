package com.example.Netflix.Subtitle;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
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

    @GetMapping(value = "/{contentId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getSubtitlesByContentId(@PathVariable Long contentId) {
        try {
            List<Subtitle> subtitles = subtitleService.getSubtitlesByContentId(contentId);
            if (subtitles.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No subtitles found for content ID: " + contentId);
            }
            return ResponseEntity.ok(subtitles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving subtitles: " + e.getMessage());
        }
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> createSubtitle(@RequestBody Subtitle subtitle) {
        try {
            String username = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
            Subtitle createdSubtitle = subtitleService.createSubtitle(subtitle);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSubtitle);
        } catch (ClassCastException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized request: Authentication failed.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid subtitle data: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating subtitle: " + e.getMessage());
        }
    }

    @DeleteMapping(value = "/{contentId}/{language}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> deleteSubtitle(@PathVariable Long contentId, @PathVariable String language) {
        try {
            String username = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
            subtitleService.deleteSubtitle(contentId, language);
            return ResponseEntity.noContent().build();
        } catch (ClassCastException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized request: Authentication failed.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request parameters: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting subtitle: " + e.getMessage());
        }
    }
}
