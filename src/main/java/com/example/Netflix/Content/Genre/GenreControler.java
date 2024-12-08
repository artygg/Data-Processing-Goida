package com.example.Netflix.Content.Genre;

import com.example.Netflix.Content.Content;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/genre")
public class GenreControler
{
    private final GenreService genreService;

    @Autowired
    public GenreControler(GenreService genreService)
    {
        this.genreService = genreService;
    }

    @PostMapping
    public ResponseEntity<Genre> createGenre(@RequestBody Genre genre) {
        try {
            System.out.println("Received POST request with content: " + genre);
            Genre savedGenre = genreService.saveGenre(genre);
            return ResponseEntity.ok(savedGenre);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(409).body(null);
        }
    }
}
