package com.example.Netflix.Genre;

import com.example.Netflix.Generalization.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenreService extends BaseService<Genre, Long> {
    private final GenreRepository genreRepository;

    @Autowired
    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Override
    protected JpaRepository<Genre, Long> getRepository() {
        return genreRepository;
    }
}
