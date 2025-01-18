package com.example.Netflix.Generalization;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public abstract class BaseService<T, ID> implements BaseServiceInterface<T,ID> {

    protected abstract JpaRepository<T, ID> getRepository();

    public T create(T entity) {
        return getRepository().save(entity);
    }

    public Optional<T> findById(ID id) {
        return getRepository().findById(id);
    }

    public List<T> findAll() {
        return getRepository().findAll();
    }

    public T update(ID id, T entity) {
        if (getRepository().existsById(id)) {
            return getRepository().save(entity);
        }
        throw new RuntimeException("Entity not found");
    }

    public void delete(ID id) {
        if (getRepository().existsById(id)) {
            getRepository().deleteById(id);
        } else {
            throw new RuntimeException("Entity not found");
        }
    }
}

