package com.example.weather.controller;

import com.example.weather.model.CreateFavouriteRequest;
import com.example.weather.model.response.FavouriteResponse;
import com.example.weather.service.FavouriteService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * HTTP API for saved favourite locations.
 */
@RestController
@RequestMapping("/api/favourites")
public class FavouriteController {

    private final FavouriteService favouriteService;

    /**
     * Creates the controller.
     *
     * @param favouriteService service used to manage favourites
     */
    public FavouriteController(FavouriteService favouriteService) {
        this.favouriteService = favouriteService;
    }

    /**
     * Saves a new favourite location.
     *
     * @param request location to store
     * @return the saved favourite
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FavouriteResponse add(
            @Valid @RequestBody CreateFavouriteRequest request) {

        return favouriteService.add(request);
    }

    /**
     * Lists every saved favourite location.
     *
     * @return all favourites
     */
    @GetMapping
    public List<FavouriteResponse> findAll() {

        return favouriteService.findAll();
    }

    /**
     * Removes a favourite by its identifier.
     *
     * @param id favourite identifier
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {

        favouriteService.delete(id);
    }
}
