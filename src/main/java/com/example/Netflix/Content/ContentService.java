package com.example.Netflix.Content;

import com.example.Netflix.Genre.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContentService
{
    private final ContentRepository contentRepository;
    private final GenreRepository genreRepository;

    @Autowired
    public ContentService(ContentRepository contentRepository, GenreRepository genreRepository) {
        this.contentRepository = contentRepository;
        this.genreRepository = genreRepository;
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

//    public void assignGenresToContent(Long contentId, List<Long> genreIds) {
//        Content content = contentRepository.findContentById(contentId)
//                .orElseThrow(() -> new RuntimeException("Content not found with ID: " + contentId));
//
//        List<Genre> genres = genreRepository.findAllById(genreIds);
//
//        if (genres.size() != genreIds.size()) {
//            throw new RuntimeException("Some genres were not found for the given IDs");
//        }
//
//        for (Genre genre : genres) {
//            GenreBridge genreBridge = new GenreBridge(content, genre);
//            try {
//                genreBridgeRepository.save(genreBridge);
//            }
//            catch (Exception e)
//            {
//                e.printStackTrace();
//            }
//        }
//    }


}
