package fr.limayrac.demo.task.infrastructure.web;

import fr.limayrac.demo.configuration.tenant.TenantContext;
import fr.limayrac.demo.dto.TaskListResponse;
import fr.limayrac.demo.dto.TaskCreateRequest;
import fr.limayrac.demo.dto.TaskResponse;
import fr.limayrac.demo.task.application.port.in.TaskUseCase;
import fr.limayrac.demo.task.domain.model.Task;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/projects/{projectId}/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks Clean Arch", description = "Operations via Clean Architecture")
public class TaskInProjectController {

    private final TaskUseCase taskUseCase;

    @Operation(summary = "Lister les tâches d'un projet")
    @GetMapping
    public Page<TaskResponse> findAllByProjectId(
            @RequestHeader(value = "X-Tenant-ID", defaultValue = "default") String tenantId,
            @PathVariable Long projectId,
            Pageable pageable
    ) {
        try {
            return taskUseCase.getTasksByProjectId(projectId, pageable, tenantId)
                    .map(this::mapToResponse);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la récupération des tâches", e);
        }
    }

    @Operation(summary = "Créer une tâche dans un projet")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse create(
            @RequestHeader(value = "X-Tenant-ID", defaultValue = "default") String tenantId,
            @PathVariable Long projectId,
            @Valid @RequestBody TaskCreateRequest request
    ) {
        try {
            Task task = new Task();
            task.setTitle(request.title());
            task.setDescription(request.description());
            task.setDueDate(request.dueDate());
            // task.setTenantId handled by service

            Task created = taskUseCase.createTask(projectId, task, tenantId);
            return mapToResponse(created);
        } catch (IllegalArgumentException e) {
             throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (RuntimeException e) {
             throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la création de la tâche", e);
        }
    }

    private TaskResponse mapToResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDueDate(),
                task.isCompleted(),
                task.getProjectId(),
                task.getCreatedOn()
        );
    }

    private TaskListResponse mapToListResponse(Task task) {
        return new TaskListResponse(
                task.getId(),
                task.getTitle(),
                task.isCompleted(),
                task.getDueDate()
        );
    }
}
