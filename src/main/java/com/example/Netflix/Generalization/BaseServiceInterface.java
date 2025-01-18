package com.example.Netflix.Generalization;

import java.util.List;
import java.util.Optional;

public interface BaseServiceInterface<T, ID> {
    Optional<T> findById(ID id);
    List<T> findAll();
    T create(T entity);
    T update(ID id, T entity);
    void delete(ID id);
}
