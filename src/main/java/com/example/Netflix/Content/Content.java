package com.example.Netflix.Content;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public abstract class Content {
    @Id
    private Integer id;
}
