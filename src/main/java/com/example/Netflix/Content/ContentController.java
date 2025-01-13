package com.example.Netflix.Content;

import com.example.Netflix.Exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contents")
public class ContentController {
    private final ContentService contentService;

    @Autowired
    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<Content> getAllContents() {
        return contentService.getAllContents();
    }

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public Content getContentById(@PathVariable Long id) throws ResourceNotFoundException {
        return contentService.getContentById(id);
    }

    @GetMapping(value = "/films", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<Content> getAllFilms() {
        return contentService.getAllFilms();
    }

    @GetMapping(value = "/series", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<Content> getAllSeries() {
        return contentService.getAllSeries();
    }

    @GetMapping(value = "/series/{seriesId}/episodes", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getEpisodesBySeriesId(@PathVariable Integer seriesId) {
        return contentService.getEpisodesBySeriesId(seriesId);
    }

    @GetMapping(value = "/genre/{genreName}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getContentsByGenre(@PathVariable String genreName) {
        return contentService.getContentsByGenre(genreName);
    }

    @GetMapping(value = "/resolution/{resolutionId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getContentsByResolution(@PathVariable Long resolutionId) {
        return contentService.getContentsByResolution(resolutionId);
    }

    @PostMapping
    public ResponseEntity<?> createContent(@RequestBody Content content) {
        try {
            ((org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal()).getUsername();
            return ResponseEntity.ok(contentService.createContent(content));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized request: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateContent(@PathVariable Long id, @RequestBody Content updatedContent)
            throws ResourceNotFoundException {
        try {
            ((org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal()).getUsername();
            return ResponseEntity.ok(contentService.updateContent(id, updatedContent));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized request: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteContent(@PathVariable Long id) {
        try {
            ((org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal()).getUsername();
            contentService.deleteContent(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized request: " + e.getMessage());
        }
    }

    @PostMapping("/series/{seriesId}/episodes")
    public ResponseEntity<?> addEpisodeToSeries(@PathVariable Integer seriesId, @RequestBody Content episode) {
        try {
            contentService.addEpisodeToSeries(seriesId, episode);
            return ResponseEntity.status(HttpStatus.CREATED).body("Episode added to series.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/{contentId}/genres/{genreId}")
    public ResponseEntity<?> addGenreToContent(@PathVariable Long contentId, @PathVariable Long genreId) throws ResourceNotFoundException
    {
        contentService.addGenreToContent(contentId, genreId);
        return ResponseEntity.ok("Genre added to content.");
    }

    @DeleteMapping("/{contentId}/genres/{genreId}")
    public ResponseEntity<?> removeGenreFromContent(@PathVariable Long contentId, @PathVariable Long genreId) throws ResourceNotFoundException
    {
        contentService.removeGenreFromContent(contentId, genreId);
        return ResponseEntity.ok("Genre removed from content.");
    }

    @PostMapping("/{contentId}/resolutions/{resolutionId}")
    public ResponseEntity<?> addResolutionToContent(@PathVariable Long contentId, @PathVariable Long resolutionId) throws ResourceNotFoundException
    {
        contentService.addResolutionToContent(contentId, resolutionId);
        return ResponseEntity.ok("Resolution added to content.");
    }

    @DeleteMapping("/{contentId}/resolutions/{resolutionId}")
    public ResponseEntity<?> removeResolutionFromContent(@PathVariable Long contentId, @PathVariable Long resolutionId) throws ResourceNotFoundException
    {
        contentService.removeResolutionFromContent(contentId, resolutionId);
        return ResponseEntity.ok("Resolution removed from content.");
    }
}