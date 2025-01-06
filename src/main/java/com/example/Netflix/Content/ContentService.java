package com.example.Netflix.Content;

import com.example.Netflix.Exceptions.ResourceNotFoundException;
import com.example.Netflix.Genre.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ContentService {
    private final ContentRepository contentRepository;

    public ContentService(ContentRepository contentRepository) {
        this.contentRepository = contentRepository;
    }

    public List<Content> getAllContents() {
        return contentRepository.findAll();
    }

    public Content getContentById(Long id) throws ResourceNotFoundException
    {

        Optional<Content> contentOptional = contentRepository.findById(Math.toIntExact(id));

        if (contentOptional.isPresent()) {
            return contentOptional.get();
        } else {
            throw new ResourceNotFoundException("Content not found with ID: " + id);
        }
    }

    public Content createContent(Content content) {
        return contentRepository.save(content);
    }

    public Content updateContent(Long id, Content updatedContent) throws ResourceNotFoundException {
        Content existingContent = getContentById(id);

        if (updatedContent.getTitle() != null) {
            existingContent.setTitle(updatedContent.getTitle());
        }
        if (updatedContent.getPoster() != null) {
            existingContent.setPoster(updatedContent.getPoster());
        }
        if (updatedContent.getDescription() != null) {
            existingContent.setDescription(updatedContent.getDescription());
        }
        if (updatedContent.getVideoLink() != null) {
            existingContent.setVideoLink(updatedContent.getVideoLink());
        }
        if (updatedContent.getDuration() != null) {
            existingContent.setDuration(updatedContent.getDuration());
        }
        if (updatedContent.getType() != null) {
            existingContent.setType(updatedContent.getType());
        }
        if (updatedContent.getSeason() != null) {
            existingContent.setSeason(updatedContent.getSeason());
        }
        if (updatedContent.getEpisodeNumber() != null) {
            existingContent.setEpisodeNumber(updatedContent.getEpisodeNumber());
        }
        if (updatedContent.getSeriesId() != null) {
            existingContent.setSeriesId(updatedContent.getSeriesId());
        }
        if (updatedContent.getGenres() != null) {
            existingContent.setGenres(updatedContent.getGenres());
        }

        existingContent.setUpdatedAt(LocalDateTime.now());

        return contentRepository.save(existingContent);
    }


    public void deleteContent(Long id) {
        contentRepository.deleteById(Math.toIntExact(id));
    }
}