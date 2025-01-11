package com.example.Netflix.Content;

import com.example.Netflix.Exceptions.ResourceNotFoundException;
import com.example.Netflix.Genre.Genre;
import com.example.Netflix.Genre.GenreRepository;
import com.example.Netflix.Resolutions.Resolution;
import com.example.Netflix.Resolutions.ResolutionRepository;
import com.example.Netflix.enums.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
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
                .orElseThrow(() -> new ResourceNotFoundException("Content not found with ID: " + id));
    }

    public List<Content> getAllFilms() {
        return contentRepository.findAllByType(ContentType.FILM);
    }

    public List<Content> getAllSeries() {
        return contentRepository.findAllByType(ContentType.SERIES);
    }

    public List<Content> getEpisodesBySeriesId(Integer seriesId) {
        return contentRepository.findAllByTypeAndSeriesId(ContentType.EPISODE, seriesId);
    }

    public List<Content> getContentsByGenre(String genreName) {
        return contentRepository.findAllByGenreName(genreName);
    }

    public List<Content> getContentsByResolution(Long resolutionId) {
        return contentRepository.findAllByResolutionId(resolutionId);
    }

    public Content createContent(Content content) {
        return contentRepository.save(content);
    }

    public Content updateContent(Long id, Content updatedContent) throws ResourceNotFoundException {
        Content existingContent = getContentById(id);
        existingContent.setTitle(updatedContent.getTitle());
        existingContent.setDescription(updatedContent.getDescription());
        existingContent.setVideoLink(updatedContent.getVideoLink());
        existingContent.setDuration(updatedContent.getDuration());
        existingContent.setType(updatedContent.getType());
        existingContent.setSeason(updatedContent.getSeason());
        existingContent.setEpisodeNumber(updatedContent.getEpisodeNumber());
        existingContent.setSeriesId(updatedContent.getSeriesId());
        existingContent.setUpdatedAt(LocalDateTime.now());
        return contentRepository.save(existingContent);
    }

    public void deleteContent(Long id) {
        contentRepository.deleteById(id);
    }

    public void addEpisodeToSeries(Integer seriesId, Content episode) {
        episode.setSeriesId(seriesId);
        episode.setType(ContentType.EPISODE);
        contentRepository.save(episode);
    }

    public void addGenreToContent(Long contentId, Long genreId) throws ResourceNotFoundException
    {
        Content content = getContentById(contentId);
        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new ResourceNotFoundException("Genre not found with ID: " + genreId));
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

