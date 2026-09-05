package com.example.weather.service;

import com.example.weather.model.CreateFavouriteRequest;
import com.example.weather.model.Favourite;
import com.example.weather.model.response.FavouriteResponse;
import com.example.weather.repository.FavouriteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Stores and removes favourite locations.
 */
@Service
public class FavouriteService {

    private final FavouriteRepository repository;

    /**
     * Creates the service.
     *
     * @param repository persistence for favourites
     */
    public FavouriteService(final FavouriteRepository repository) {
        this.repository = repository;
    }

    /**
     * Saves a favourite location.
     *
     * @param request location to store
     * @return the saved favourite
     */
    @Transactional
    public FavouriteResponse add(CreateFavouriteRequest request) {

        Favourite favourite = repository.save(new Favourite(request.location()));

        return new FavouriteResponse(
                favourite.getId(),
                favourite.getLocation()
        );
    }

    /**
     * Returns every saved favourite location.
     *
     * @return all favourites
     */
    @Transactional(readOnly = true)
    public List<FavouriteResponse> findAll() {

        return repository.findAll()
                .stream()
                .map(favourite ->
                        new FavouriteResponse(
                                favourite.getId(),
                                favourite.getLocation()))
                .toList();
    }

    /**
     * Deletes a favourite by identifier.
     *
     * @param id favourite identifier
     * @throws IllegalArgumentException if no favourite exists for {@code id}
     */
    @Transactional
    public void delete(Long id) {

        if (!repository.existsById(id)) {
            throw new IllegalArgumentException(
                    "Favourite does not exist: " + id);
        }
        repository.deleteById(id);
    }
}
