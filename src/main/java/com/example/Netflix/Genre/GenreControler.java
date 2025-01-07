package com.example.Netflix.Genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @GetMapping
    public List<Genre> getAllGenres() {
        return genreService.getAllGenres();
    }

    @PostMapping
    public ResponseEntity<Genre> createGenre(@RequestBody Genre genre) {
        try {
            Genre savedGenre = genreService.saveGenre(genre);
            return ResponseEntity.ok(savedGenre);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(409).body(null);
        }
    }
}
