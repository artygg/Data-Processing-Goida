package com.example.Netflix.Content;

import com.example.Netflix.Exceptions.ResourceNotFoundException;
import com.example.Netflix.Generalization.BaseController;
import com.example.Netflix.Generalization.BaseService;
import com.example.Netflix.JSON.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
        try {
            return contentService.getEpisodesBySeriesId(seriesId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Bad request"));
        }

    }

    @GetMapping(value = "/genre/{genreName}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getContentsByGenre(@PathVariable String genreName) {
        try {
            return contentService.getContentsByGenre(genreName);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Bad request"));
        }
    }

    @GetMapping(value = "/resolution/{resolutionId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getContentsByResolution(@PathVariable Long resolutionId) {
        try {
            return contentService.getContentsByResolution(resolutionId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Bad request"));
        }
    }

    @PostMapping("/series/{seriesId}/episodes")
    public ResponseEntity<?> addEpisodeToSeries(@PathVariable Integer seriesId, @RequestBody Content episode) {
        try {
            contentService.addEpisodeToSeries(seriesId, episode);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Bad request"));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("Episode added to series.");
    }

    @PostMapping("/{contentId}/genres/{genreId}")
    public ResponseEntity<?> addGenreToContent(@PathVariable Long contentId, @PathVariable Long genreId) throws ResourceNotFoundException {
        try {
            return contentService.addGenreToContent(contentId, genreId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Bad request"));
        }
    }

    @DeleteMapping("/{contentId}/genres/{genreId}")
    public ResponseEntity<?> removeGenreFromContent(@PathVariable Long contentId, @PathVariable Long genreId) throws ResourceNotFoundException {
        try {
            contentService.removeGenreFromContent(contentId, genreId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Bad request"));
        }
        return ResponseEntity.ok("Genre removed from content.");
    }

    @PostMapping("/{contentId}/resolutions/{resolutionId}")
    public ResponseEntity<?> addResolutionToContent(@PathVariable Long contentId, @PathVariable Long resolutionId) throws ResourceNotFoundException {
        try {
            return contentService.addResolutionToContent(contentId, resolutionId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Bad request"));
        }
    }

    @DeleteMapping("/{contentId}/resolutions/{resolutionId}")
    public ResponseEntity<?> removeResolutionFromContent(@PathVariable Long contentId, @PathVariable Long resolutionId) throws ResourceNotFoundException {
        try {
            contentService.removeResolutionFromContent(contentId, resolutionId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Bad request"));
        }
        return ResponseEntity.ok("Resolution removed from content.");
    }
}