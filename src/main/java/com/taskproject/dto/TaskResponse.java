package com.taskproject.dto;

import com.taskproject.enums.TaskPriority;
import com.taskproject.enums.TaskStatus;
import java.time.Instant;

/**
 * DTO sent back to client for a task.
 */
public class TaskResponse {
    private Long id;
    private String taskName;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private Instant createdAt;
    private Instant updatedAt;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
    public TaskPriority getPriority() { return priority; }
    public void setPriority(TaskPriority priority) { this.priority = priority; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
