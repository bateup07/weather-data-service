package com.example.weather.repository;

import com.example.weather.model.Favourite;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Persistence access for favourite locations.
 */
public interface FavouriteRepository extends JpaRepository<Favourite, Long> {

}
