package com.example.Netflix.Content;

import com.example.Netflix.Exceptions.ResourceNotFoundException;
import com.example.Netflix.Generalization.BaseController;
import com.example.Netflix.Generalization.BaseService;
import com.example.Netflix.Generalization.BaseServiceInterface;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contents")
public class ContentController extends BaseController<Content, Long> {
    private final ContentService contentService;

    @Autowired
    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

    @Override
    protected BaseService<Content, Long> getService() {
        return contentService;
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
    public ResponseEntity<?> addGenreToContent(@PathVariable Long contentId, @PathVariable Long genreId) throws ResourceNotFoundException {
        contentService.addGenreToContent(contentId, genreId);

        return ResponseEntity.ok("Genre added to content.");
    }

    @DeleteMapping("/{contentId}/genres/{genreId}")
    public ResponseEntity<?> removeGenreFromContent(@PathVariable Long contentId, @PathVariable Long genreId) throws ResourceNotFoundException {
        contentService.removeGenreFromContent(contentId, genreId);

        return ResponseEntity.ok("Genre removed from content.");
    }

    @PostMapping("/{contentId}/resolutions/{resolutionId}")
    public ResponseEntity<?> addResolutionToContent(@PathVariable Long contentId, @PathVariable Long resolutionId) throws ResourceNotFoundException {
        contentService.addResolutionToContent(contentId, resolutionId);

        return ResponseEntity.ok("Resolution added to content.");
    }

    @DeleteMapping("/{contentId}/resolutions/{resolutionId}")
    public ResponseEntity<?> removeResolutionFromContent(@PathVariable Long contentId, @PathVariable Long resolutionId) throws ResourceNotFoundException {
        contentService.removeResolutionFromContent(contentId, resolutionId);

        return ResponseEntity.ok("Resolution removed from content.");
    }
}