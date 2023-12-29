package com.nikola.userhandlerbe2.repositories;

import com.nikola.userhandlerbe2.entities.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByFirstName(String firstName);
    Optional<User> findByLastName(String lastName);
}
