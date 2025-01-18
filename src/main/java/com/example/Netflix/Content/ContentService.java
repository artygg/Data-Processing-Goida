package com.example.Netflix.Content;

import com.example.Netflix.Exceptions.ResourceNotFoundException;
import com.example.Netflix.Genre.Genre;
import com.example.Netflix.Genre.GenreRepository;
import com.example.Netflix.Resolutions.Resolution;
import com.example.Netflix.Resolutions.ResolutionRepository;
import com.example.Netflix.enums.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ContentService {
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

    public List<Content> getAllContents() {
        return contentRepository.findAll();
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

    public Content createContent(Content content) {
        return contentRepository.save(content);
    }

    public ResponseEntity<?> updateContent(Long id, Content updatedContent)
    {
        try {
            System.out.println("Type: " + updatedContent.getType());
            contentRepository.updateContentById(
                    id,
                    updatedContent.getTitle(),
                    updatedContent.getDescription(),
                    updatedContent.getVideoLink(),
                    updatedContent.getDuration(),
                    updatedContent.getType() != null ? updatedContent.getType().toString().toUpperCase() : null,
                    updatedContent.getSeason(),
                    updatedContent.getEpisodeNumber(),
                    updatedContent.getSeriesId()
            );

            return ResponseEntity.ok(contentRepository.findContentById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to update content: " + e.getMessage());
        }
    }

    public void deleteContent(Long id) {
        contentRepository.deleteById(id);
    }

    public void addEpisodeToSeries(Integer seriesId, Content episode) {
        episode.setSeriesId(seriesId);
        episode.setType(ContentType.EPISODE);
        contentRepository.save(episode);
    }

    public Optional<Content> findById(Long id) {
        return contentRepository.findById(id);
    }

    public void addGenreToContent(Long contentId, Long genreId) throws ResourceNotFoundException
    {
        Content content = getContentById(contentId);
        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new ResourceNotFoundException("Genre not found"));
        content.getGenres().add(genre);
        contentRepository.save(content);
    }

    public void removeGenreFromContent(Long contentId, Long genreId) throws ResourceNotFoundException
    {
        Content content = getContentById(contentId);
        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new ResourceNotFoundException("Genre not found with ID: " + genreId));
        content.getGenres().remove(genre);
        contentRepository.save(content);
    }

    public void addResolutionToContent(Long contentId, Long resolutionId) throws ResourceNotFoundException
    {
        Content content = getContentById(contentId);
        Resolution resolution = resolutionRepository.findById(Math.toIntExact(resolutionId))
                .orElseThrow(() -> new ResourceNotFoundException("Resolution not found with ID: " + resolutionId));
        content.getResolutions().add(resolution);
        contentRepository.save(content);
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
