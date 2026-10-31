package com.taskproject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskproject.dto.AuthResponse;
import com.taskproject.dto.LoginRequest;
import com.taskproject.dto.RegisterRequest;
import com.taskproject.dto.TaskRequest;
import com.taskproject.dto.TaskResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests covering authentication flow and the secured task CRUD operations.
 * Uses the in‑memory H2 database (profile "test").
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthAndTaskIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String jwtToken;

    @BeforeEach
    void setUp() throws Exception {
        // Register a test user
        RegisterRequest register = new RegisterRequest();
        register.setEmail("test@example.com");
        register.setPassword("Password123!");
        register.setFirstName("Test");
        register.setLastName("User");
        register.setRole(com.taskproject.enums.Role.USER);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isCreated());

        // Login to obtain JWT token
        LoginRequest login = new LoginRequest();
        login.setEmail("test@example.com");
        login.setPassword("Password123!");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        AuthResponse authResponse = objectMapper.readValue(loginResult.getResponse().getContentAsString(), AuthResponse.class);
        this.jwtToken = "Bearer " + authResponse.getToken();
    }

    @Nested
    @DisplayName("Task CRUD flow")
    class TaskCrud {
        private Long createdTaskId;

        @Test
        @DisplayName("Create, retrieve, update and delete a task")
        void fullCrud() throws Exception {
            // ---------- CREATE ----------
            TaskRequest createReq = new TaskRequest();
            createReq.setTaskName("Sample Task");
            createReq.setDescription("A task for integration testing");
            createReq.setStatus(com.taskproject.enums.TaskStatus.PENDING);
            createReq.setPriority(com.taskproject.enums.TaskPriority.MEDIUM);

            MvcResult createResult = mockMvc.perform(post("/api/tasks")
                            .header("Authorization", jwtToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createReq)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists())
                    .andReturn();

            TaskResponse created = objectMapper.readValue(createResult.getResponse().getContentAsString(), TaskResponse.class);
            createdTaskId = created.getId();
            assertThat(created.getTaskName()).isEqualTo("Sample Task");

            // ---------- READ ALL ----------
            mockMvc.perform(get("/api/tasks")
                            .header("Authorization", jwtToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(createdTaskId.intValue()));

            // ---------- READ ONE ----------
            mockMvc.perform(get("/api/tasks/{id}", createdTaskId)
                            .header("Authorization", jwtToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.taskName").value("Sample Task"));

            // ---------- UPDATE ----------
            TaskRequest updateReq = new TaskRequest();
            updateReq.setTaskName("Updated Task");
            updateReq.setDescription("Updated description");
            updateReq.setStatus(com.taskproject.enums.TaskStatus.IN_PROGRESS);
            updateReq.setPriority(com.taskproject.enums.TaskPriority.HIGH);

            mockMvc.perform(put("/api/tasks/{id}", createdTaskId)
                            .header("Authorization", jwtToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateReq)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.taskName").value("Updated Task"))
                    .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

            // ---------- DELETE ----------
            mockMvc.perform(delete("/api/tasks/{id}", createdTaskId)
                            .header("Authorization", jwtToken))
                    .andExpect(status().isNoContent());

            // Verify deletion
            mockMvc.perform(get("/api/tasks/{id}", createdTaskId)
                            .header("Authorization", jwtToken))
                    .andExpect(status().isNotFound());
        }
    }
}
