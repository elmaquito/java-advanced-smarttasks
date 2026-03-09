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

@RestController
@RequestMapping("/api/projects/{projectId}/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks Clean Arch", description = "Operations via Clean Architecture")
public class TaskInProjectController {

    private final TaskUseCase taskUseCase;

    @Operation(summary = "Lister les tâches d'un projet")
    @GetMapping
    public Page<TaskListResponse> findAllByProjectId(@PathVariable Long projectId, Pageable pageable) {
        return taskUseCase.getTasksByProjectId(projectId, pageable, TenantContext.getTenant())
                .map(this::mapToListResponse);
    }

    @Operation(summary = "Créer une tâche dans un projet")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse create(@PathVariable Long projectId, @Valid @RequestBody TaskCreateRequest request) {
        Task task = new Task();
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setDueDate(request.dueDate());
        // task.setTenantId handled by service

        Task created = taskUseCase.createTask(projectId, task, TenantContext.getTenant());
        return mapToResponse(created);
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
