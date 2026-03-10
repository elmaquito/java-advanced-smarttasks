package fr.limayrac.demo.task.infrastructure.web;

import fr.limayrac.demo.configuration.tenant.TenantContext;
import fr.limayrac.demo.domain.Attachment;
import fr.limayrac.demo.dto.TaskCreateRequest;
import fr.limayrac.demo.dto.TaskRequest;
import fr.limayrac.demo.dto.TaskResponse;
import fr.limayrac.demo.task.application.port.in.TaskUseCase;
import fr.limayrac.demo.task.domain.model.Task;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Operations sur les tâches (Clean Arch)")
public class TaskController {

    private final TaskUseCase taskUseCase;

    @Operation(summary = "Lister toutes les tâches")
    @GetMapping
    public List<TaskResponse> findAll(
            @RequestHeader(value = "X-Tenant-ID", defaultValue = "default") String tenantId) {
        return taskUseCase.getAllTasks(tenantId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Récupérer une tâche")
    @GetMapping("/{id}")
    public TaskResponse findById(
            @PathVariable Long id,
            @RequestHeader(value = "X-Tenant-ID", defaultValue = "default") String tenantId) {
        return taskUseCase.getTaskById(id, tenantId)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
    }

    @Operation(summary = "Créer une tâche")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse create(@Valid @RequestBody TaskRequest request) {
       throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Use project scoped creation");
    }

    @Operation(summary = "Mettre à jour une tâche")
    @PutMapping("/{id}")
    public TaskResponse update(
            @PathVariable Long id, 
            @Valid @RequestBody TaskRequest request,
            @RequestHeader(value = "X-Tenant-ID", defaultValue = "default") String tenantId) {
        Task taskUpdates = new Task();
        taskUpdates.setTitle(request.title());
        taskUpdates.setDescription(request.description());
        taskUpdates.setDueDate(request.dueDate());
        // taskUpdates.setCompleted(request.completed()); // Check TaskRequest for completed field

        Task updated = taskUseCase.updateTask(id, taskUpdates, tenantId);
        return mapToResponse(updated);
    }

    @Operation(summary = "Supprimer une tâche")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id,
            @RequestHeader(value = "X-Tenant-ID", defaultValue = "default") String tenantId) {
        taskUseCase.deleteTask(id, tenantId);
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
}
