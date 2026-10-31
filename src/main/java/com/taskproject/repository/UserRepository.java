package com.taskproject.repository;

import com.taskproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data repository for {@link User} entities.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Find a user by email – used for login and duplicate‑email checks.
     */
    Optional<User> findByEmail(String email);
}
