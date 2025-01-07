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

    public Content getContentById(Long id) {
        return contentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found with ID: " + id));
    }

    public List<Content> getContentsByGenre(String genreName) {
        return contentRepository.findAllByGenreName(genreName);
    }

    public Content createContent(Content content) {
        return contentRepository.save(content);
    }

    public Content updateContent(Long id, Content updatedContent) {
        Content existingContent = getContentById(id);

        existingContent.setTitle(updatedContent.getTitle());
        existingContent.setPoster(updatedContent.getPoster());
        existingContent.setDescription(updatedContent.getDescription());
        existingContent.setVideoLink(updatedContent.getVideoLink());
        existingContent.setDuration(updatedContent.getDuration());
        existingContent.setType(updatedContent.getType());
        existingContent.setSeason(updatedContent.getSeason());
        existingContent.setEpisodeNumber(updatedContent.getEpisodeNumber());
        existingContent.setSeriesId(updatedContent.getSeriesId());
        existingContent.setUpdatedAt(LocalDateTime.now());

        if (updatedContent.getGenres() != null) {
            existingContent.setGenres(updatedContent.getGenres());
        }

        return contentRepository.save(existingContent);
    }


    public void deleteContent(Long id) {
        contentRepository.deleteById(id);
    }
}