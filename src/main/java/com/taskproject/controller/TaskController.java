package com.taskproject.controller;

import com.taskproject.dto.TaskRequest;
import com.taskproject.dto.TaskResponse;
import com.taskproject.entity.User;
import com.taskproject.security.CustomUserDetails;
import com.taskproject.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for task CRUD operations. All endpoints are protected by JWT and operate on
 * the tasks belonging to the currently authenticated user (extracted via {@code @AuthenticationPrincipal}).
 */
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Create, retrieve, update and delete your personal tasks.")
@SecurityRequirement(name = "BearerAuth")
public class TaskController {

    private final TaskService taskService;

    @Operation(summary = "Create a new task")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Task created",
                     content = @Content(schema = @Schema(implementation = TaskResponse.class))),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized – missing or invalid JWT")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(@Valid @RequestBody TaskRequest request,
                                   @AuthenticationPrincipal CustomUserDetails principal) {
        User user = principal.getUser();
        return taskService.createTask(request, user);
    }

    @Operation(summary = "List all tasks belonging to the authenticated user")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Task list returned",
                     content = @Content(array = @ArraySchema(schema = @Schema(implementation = TaskResponse.class)))),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public List<TaskResponse> getAllTasks(@AuthenticationPrincipal CustomUserDetails principal) {
        User user = principal.getUser();
        return taskService.getAllTasks(user);
    }

    @Operation(summary = "Get a single task by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Task found",
                     content = @Content(schema = @Schema(implementation = TaskResponse.class))),
        @ApiResponse(responseCode = "404", description = "Task not found or not owned by you"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/{id}")
    public TaskResponse getTask(
            @Parameter(description = "Task ID", example = "1") @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal) {
        User user = principal.getUser();
        return taskService.getTaskById(id, user);
    }

    @Operation(summary = "Update a task (full replacement)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Task updated",
                     content = @Content(schema = @Schema(implementation = TaskResponse.class))),
        @ApiResponse(responseCode = "404", description = "Task not found or not owned by you"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping("/{id}")
    public TaskResponse updateTask(
            @Parameter(description = "Task ID", example = "1") @PathVariable Long id,
            @Valid @RequestBody TaskRequest request,
            @AuthenticationPrincipal CustomUserDetails principal) {
        User user = principal.getUser();
        return taskService.updateTask(id, request, user);
    }

    @Operation(summary = "Delete a task by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Task deleted"),
        @ApiResponse(responseCode = "404", description = "Task not found or not owned by you"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(
            @Parameter(description = "Task ID", example = "1") @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal) {
        User user = principal.getUser();
        taskService.deleteTask(id, user);
    }
}
