package com.webapp.webapp.repository;

import com.webapp.webapp.repository.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    /**
     * Fetches the User based on the Email
     * @param email a.k.a username
     * @return User if exists.
     */
    Optional<User> findByEmail(String email);
}
