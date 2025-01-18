package com.example.Netflix.Genre;

import com.example.Netflix.Generalization.BaseController;
import com.example.Netflix.Generalization.BaseService;
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
public class GenreControler extends BaseController<Genre, Long> {
    private final GenreService genreService;

    @Autowired
    public GenreControler(GenreService genreService) {
        this.genreService = genreService;
    }

    @Override
    protected BaseService<Genre, Long> getService() {
        return genreService;
    }
}
