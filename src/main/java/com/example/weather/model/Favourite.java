package com.example.weather.model;

import jakarta.persistence.*;

/**
 * Persisted favourite location.
 */
@Entity
@Table(name = "favourites")
public class Favourite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String location;

    protected Favourite() {
    }

    /**
     * Creates a favourite for the given place name.
     *
     * @param location place name to store
     */
    public Favourite(String location) {
        this.location = location;
    }

    /**
     * Returns the generated identifier, or {@code null} before the entity is saved.
     *
     * @return favourite identifier
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns the saved place name.
     *
     * @return favourite location
     */
    public String getLocation() {
        return location;
    }
}
