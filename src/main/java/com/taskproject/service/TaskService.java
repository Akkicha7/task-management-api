package com.taskproject.service;

import com.taskproject.dto.TaskRequest;
import com.taskproject.dto.TaskResponse;
import com.taskproject.entity.Task;
import com.taskproject.entity.User;
import com.taskproject.exception.ResourceNotFoundException;
import com.taskproject.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer handling business logic for {@link Task} management.
 * All operations are scoped to the authenticated {@link User} ensuring ownership.
 */
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ModelMapper modelMapper;

    /**
     * Create a new task for the given user.
     */
    public TaskResponse createTask(TaskRequest request, User user) {
        Task task = modelMapper.map(request, Task.class);
        task.setUser(user);
        Task saved = taskRepository.save(task);
        return modelMapper.map(saved, TaskResponse.class);
    }

    /**
     * Retrieve all tasks belonging to the given user.
     */
    public List<TaskResponse> getAllTasks(User user) {
        return taskRepository.findByUser(user)
                .stream()
                .map(task -> modelMapper.map(task, TaskResponse.class))
                .collect(Collectors.toList());
    }

    /**
     * Retrieve a specific task by id for the given user.
     */
    public TaskResponse getTaskById(Long id, User user) {
        Task task = taskRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        return modelMapper.map(task, TaskResponse.class);
    }

    /**
     * Update an existing task. Only the owner can update.
     */
    public TaskResponse updateTask(Long id, TaskRequest request, User user) {
        Task task = taskRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        // Map non-null fields from request onto the existing entity.
        modelMapper.map(request, task);
        Task updated = taskRepository.save(task);
        return modelMapper.map(updated, TaskResponse.class);
    }

    /**
     * Delete a task owned by the user.
     */
    public void deleteTask(Long id, User user) {
        Task task = taskRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        taskRepository.delete(task);
    }
}
