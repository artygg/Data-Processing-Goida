package com.example.Netflix.Content;

import com.example.Netflix.Exceptions.ResourceNotFoundException;
import com.example.Netflix.Generalization.BaseService;
import com.example.Netflix.Genre.Genre;
import com.example.Netflix.Genre.GenreRepository;
import com.example.Netflix.JSON.ResponseMessage;
import com.example.Netflix.Resolutions.Resolution;
import com.example.Netflix.Resolutions.ResolutionRepository;
import com.example.Netflix.enums.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ContentService extends BaseService<Content, Long> {
    private final ContentRepository contentRepository;
    private final GenreRepository genreRepository;
    private final ResolutionRepository resolutionRepository;

    @Autowired
    public ContentService(ContentRepository contentRepository, GenreRepository genreRepository,
                          ResolutionRepository resolutionRepository) {
        this.contentRepository = contentRepository;
        this.genreRepository = genreRepository;
        this.resolutionRepository = resolutionRepository;
    }

    @Override
    protected JpaRepository<Content, Long> getRepository() {
        return contentRepository;
    }

    public Content getContentById(Long id) throws ResourceNotFoundException {
        return contentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found"));
    }

    public List<Content> getAllFilms() {
        return contentRepository.findAllByType(ContentType.FILM);
    }

    public List<Content> getAllSeries() {
        return contentRepository.findAllByType(ContentType.SERIES);
    }

    public ResponseEntity<?> getEpisodesBySeriesId(Integer seriesId) {
        try {
            return ResponseEntity.ok(contentRepository.findAllByTypeAndSeriesId(ContentType.EPISODE, seriesId));
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    public ResponseEntity<?> getContentsByGenre(String genreName) {
        try {
            return ResponseEntity.ok(contentRepository.findAllByGenreName(genreName));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    public ResponseEntity<?> getContentsByResolution(Long resolutionId) {
        try {
            return ResponseEntity.ok(contentRepository.findAllByResolutionId(resolutionId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    public void addEpisodeToSeries(Integer seriesId, Content episode) {
        episode.setSeriesId(seriesId);
        episode.setType(ContentType.EPISODE);
        contentRepository.save(episode);
    }

    public Optional<Content> findById(Long id) {
        return contentRepository.findById(id);
    }

    public ResponseEntity<?> addGenreToContent(Long contentId, Long genreId) throws ResourceNotFoundException
    {
        Content content = getContentById(contentId);
        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new ResourceNotFoundException("Genre not found"));
        content.getGenres().add(genre);
        try {
            contentRepository.save(content);
            return ResponseEntity.status(HttpStatus.CREATED).body(content);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Bad request"));
        }
    }

    public void removeGenreFromContent(Long contentId, Long genreId) throws ResourceNotFoundException
    {
        Content content = getContentById(contentId);
        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new ResourceNotFoundException("Genre not found with ID: " + genreId));
        content.getGenres().remove(genre);
        contentRepository.save(content);
    }

    public ResponseEntity<?> addResolutionToContent(Long contentId, Long resolutionId) throws ResourceNotFoundException
    {
        Content content = getContentById(contentId);
        Resolution resolution = resolutionRepository.findById(Math.toIntExact(resolutionId))
                .orElseThrow(() -> new ResourceNotFoundException("Resolution not found with ID: " + resolutionId));
        content.getResolutions().add(resolution);

        try {
            contentRepository.save(content);
            return ResponseEntity.ok(content);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Bad request"));
        }
    }

    public void removeResolutionFromContent(Long contentId, Long resolutionId) throws ResourceNotFoundException
    {
        Content content = getContentById(contentId);
        Resolution resolution = resolutionRepository.findById(Math.toIntExact(resolutionId))
                .orElseThrow(() -> new ResourceNotFoundException("Resolution not found with ID: " + resolutionId));
        content.getResolutions().remove(resolution);
        contentRepository.save(content);
    }
}
