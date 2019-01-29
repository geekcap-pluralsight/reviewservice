package com.pluralsight.reviewservice.service;

import com.pluralsight.reviewservice.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    /**
     * Returns the review with the specified ID.
     * @param id        The ID of the review to return.
     * @return          The review with the specified ID.
     */
    Optional<Review> findById(String id);

    /**
     * Returns the review with the specified product ID.
     * @param productId The product ID for which to return the review.
     * @return          The review for the specified product ID.
     */
    Optional<Review> findByProductId(Integer productId);

    /**
     * Returns all reviews in the database.
     * @return          All reviews in the database.
     */
    List<Review> findAll();

    /**
     * Saves the specified review to the database.
     * @param review    The review to save.
     * @return          The saved review, including a newly generated ID.
     */
    Review save(Review review);

    /**
     * Updates the specified review in the database.
     * @param review    The review to update.
     * @return          The updated review.
     */
    Review update(Review review);

    /**
     * Deletes the review with the specified ID.
     * @param id        The ID of the review to delete.
     */
    void delete(String id);
}
