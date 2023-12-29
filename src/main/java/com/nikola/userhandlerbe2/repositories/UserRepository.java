package com.nikola.userhandlerbe2.repositories;

import com.nikola.userhandlerbe2.entities.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, Long> {

    User findByEmail(String email);
    User findByFirstName(String firstName);
    User findByLastName(String lastName);
}
