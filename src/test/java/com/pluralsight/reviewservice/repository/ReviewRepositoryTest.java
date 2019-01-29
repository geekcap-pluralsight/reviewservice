package com.pluralsight.reviewservice.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pluralsight.reviewservice.model.Review;
import com.pluralsight.reviewservice.model.ReviewEntry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@DataMongoTest
class ReviewRepositoryTest {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ReviewRepository repository;

    /**
     * Jackson ObjectMapper: used to load a JSON file into a list of Reviews
     */
    private ObjectMapper mapper = new ObjectMapper();

    /**
     * The path to our Sample JSON file.
     */
    private static File SAMPLE_JSON = Paths.get("src", "test", "resources", "data", "sample.json").toFile();

    @BeforeEach
    void beforeEach() throws Exception {
        // Deserialize our JSON file to an array of reviews
        Review[] objects = mapper.readValue(SAMPLE_JSON, Review[].class);

        // Load each review into MongoDB
        Arrays.stream(objects).forEach(mongoTemplate::save);
    }

    @AfterEach
    void afterEach() {
        // Drop the reviews collection so we can start fresh
        mongoTemplate.dropCollection("Reviews");
    }

    @Test
    void testFindAll() {
        List<Review> reviews = repository.findAll();
        Assertions.assertEquals(2, reviews.size(), "Should be two reviews in the database");
    }

    @Test
    void testFindByIdSuccess() {
        Optional<Review> review = repository.findById("1");
        Assertions.assertTrue(review.isPresent(), "We should have found a review with ID 1");
        review.ifPresent(r -> {
            Assertions.assertEquals("1", r.getId(), "Review ID should be 1");
            Assertions.assertEquals(1, r.getProductId().intValue(), "Review Product ID should be 1");
            Assertions.assertEquals(1, r.getVersion().intValue(), "Review version should be 1");
            Assertions.assertEquals(1, r.getEntries().size(), "Review 1 should have one entry");
        });
    }

    @Test
    void testFindByIdFailure() {
        Optional<Review> review = repository.findById("99");
        Assertions.assertFalse(review.isPresent(), "We should not find a review with ID 99");
    }

    @Test
    void testFindByProductIdSuccess() {
        Optional<Review> review = repository.findByProductId(1);
        Assertions.assertTrue(review.isPresent(), "There should be a review for product ID 1");
    }

    @Test
    void testFindByProductIdFailure() {
        Optional<Review> review = repository.findByProductId(99);
        Assertions.assertFalse(review.isPresent(), "There should not be a review for product ID 99");
    }

    @Test
    void testSave() {
        // Create a test Review
        Review review = new Review(10, 1);
        review.getEntries().add(new ReviewEntry("test-user", new Date(), "This is a review"));

        // Persist the review to MongoDB
        Review savedReview = repository.save(review);

        // Retrieve the review
        Optional<Review> loadedReview = repository.findById(savedReview.getId());

        // Validations
        Assertions.assertTrue(loadedReview.isPresent());
        loadedReview.ifPresent(r -> {
            Assertions.assertEquals(10, r.getProductId().intValue());
            Assertions.assertEquals(1, r.getVersion().intValue(), "Review version should be 1");
            Assertions.assertEquals(1, r.getEntries().size(), "Review 1 should have one entry");
        });
    }

    @Test
    void testUpdate() {
        // Retrieve review 2
        Optional<Review> review = repository.findById("2");
        Assertions.assertTrue(review.isPresent(), "Review 2 should be present");
        Assertions.assertEquals(3, review.get().getEntries().size(), "There should be 3 review entries");

        // Add an entry to the review and save
        Review reviewToUpdate = review.get();
        reviewToUpdate.getEntries().add(new ReviewEntry("test-user-2", new Date(), "This is a fourth review"));
        repository.save(reviewToUpdate);

        // Retrieve the review again and validate that it now has 4 entries
        Optional<Review> updatedReview = repository.findById("2");
        Assertions.assertTrue(updatedReview.isPresent(), "Review 2 should be present");
        Assertions.assertEquals(4, updatedReview.get().getEntries().size(), "There should be 3 review entries");
    }

    @Test
    void testDelete() {
        // Delete review 2
        repository.deleteById("2");

        // Confirm that it is no longer in the database
        Optional<Review> review = repository.findById("2");
        Assertions.assertFalse(review.isPresent(), "Review 2 should now be deleted from the database");
    }
}
