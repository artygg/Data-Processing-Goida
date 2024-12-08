package com.example.Netflix.Content.Genre;

import org.springframework.beans.factory.annotation.Autowired;
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
}
