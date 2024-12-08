package com.example.Netflix.Content;

import com.example.Netflix.Content.Genre.Genre;
import com.example.Netflix.Content.Genre.GenreRepository;
import com.example.Netflix.Content.GenreBridge.GenreBridge;
import com.example.Netflix.Content.GenreBridge.GenreBridgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContentService
{
    private final ContentRepository contentRepository;
    private final GenreRepository genreRepository;
    private final GenreBridgeRepository genreBridgeRepository;

    @Autowired
    public ContentService(ContentRepository contentRepository, GenreRepository genreRepository,  GenreBridgeRepository genreBridgeRepository) {
        this.contentRepository = contentRepository;
        this.genreRepository = genreRepository;
        this.genreBridgeRepository = genreBridgeRepository;
    }


    public List<Content> getAllContents()
    {
        return contentRepository.findAll();
    }


    public Content saveContent(Content content)
    {
        return contentRepository.save(content);
    }

    public void deleteContent(int id) {
        contentRepository.deleteById(id);
    }

    public void assignGenresToContent(Integer contentId, List<Long> genreIds) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("Content not found"));

        List<Genre> genres = genreRepository.findAllById(genreIds);

        if (genres.isEmpty()) {
            throw new RuntimeException("No genres found for the given IDs");
        }

        for (Genre genre : genres) {
            GenreBridge genreBridge = new GenreBridge();
            genreBridge.setContent(content);
            genreBridge.setGenre(genre);

            genreBridgeRepository.save(genreBridge);
        }
    }

}
