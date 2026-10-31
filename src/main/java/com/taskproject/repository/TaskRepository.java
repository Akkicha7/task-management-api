package com.taskproject.repository;

import com.taskproject.entity.Task;
import com.taskproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link Task} entities.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    /**
     * Find all tasks belonging to a specific user.
     */
    List<Task> findByUser(User user);

    /**
     * Find a task by its id and owning user – used for ownership checks.
     */
    Optional<Task> findByIdAndUser(Long id, User user);
}
