package com.pluralsight.reviewservice.repository;

import com.pluralsight.reviewservice.model.Review;
import com.pluralsight.reviewservice.model.ReviewEntry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@DataMongoTest
@ExtendWith(MongoSpringExtension.class)
class ReviewRepositoryTestMoreElegant {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ReviewRepository repository;

    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    @Test
    @MongoDataFile(value = "sample.json", classType = Review.class, collectionName = "Reviews")
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
    @MongoDataFile(value = "sample.json", classType = Review.class, collectionName = "Reviews")
    void testFindAll() {
        List<Review> reviews = repository.findAll();
        Assertions.assertEquals(2, reviews.size(), "Should be two reviews in the database");
        reviews.forEach(System.out::println);
    }

    @Test
    @MongoDataFile(value = "sample6.json", classType = Review.class, collectionName = "Reviews")
    void testFindAll6() {
        List<Review> reviews = repository.findAll();
        Assertions.assertEquals(6, reviews.size(), "Should be six reviews in the database");
        reviews.forEach(System.out::println);
    }

    @Test
    @MongoDataFile(value = "sample.json", classType = Review.class, collectionName = "Reviews")
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
    @MongoDataFile(value = "sample.json", classType = Review.class, collectionName = "Reviews")
    void testFindByIdFailure() {
        Optional<Review> review = repository.findById("99");
        Assertions.assertFalse(review.isPresent(), "We should not find a review with ID 99");
    }

    @Test
    @MongoDataFile(value = "sample.json", classType = Review.class, collectionName = "Reviews")
    void testFindByProductIdSuccess() {
        Optional<Review> review = repository.findByProductId(1);
        Assertions.assertTrue(review.isPresent(), "There should be a review for product ID 1");
    }

    @Test
    @MongoDataFile(value = "sample.json", classType = Review.class, collectionName = "Reviews")
    void testFindByProductIdFailure() {
        Optional<Review> review = repository.findByProductId(99);
        Assertions.assertFalse(review.isPresent(), "There should not be a review for product ID 99");
    }
}
