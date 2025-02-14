package org.samaan.repositories;

import org.samaan.model.Trip;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TripRepository extends MongoRepository<Trip, String> {
    List<Trip> findByEmail(String email);

    List<Trip> findBySourceAndDestinationAndDate(String source, String destination, LocalDate date);
}
