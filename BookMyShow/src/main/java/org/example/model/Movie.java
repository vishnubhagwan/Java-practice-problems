package org.example.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "movie")
public class Movie {
    @GeneratedValue
    @Id
    Long id;

    String title;

    Integer duration;

    String genre;

    String rating;
}
