package com.example.Netflix.Genre;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/genres")
public class GenreControler {
    private final GenreService genreService;

    @Autowired
    public GenreControler(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<Genre> getAllGenres() {
        return genreService.getAllGenres();
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> createGenre(@RequestBody @Valid Genre genre) {
        try {
            ((org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal()).getUsername();

            try {
                Genre savedGenre = genreService.saveGenre(genre);
                return ResponseEntity.ok(savedGenre);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(409).body(null);
            }
        } catch (ClassCastException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized request");
        }
    }
}
