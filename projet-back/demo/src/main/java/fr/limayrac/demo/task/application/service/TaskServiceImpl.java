package fr.limayrac.demo.task.application.service;

import fr.limayrac.demo.task.application.port.in.TaskUseCase;
import fr.limayrac.demo.task.application.port.out.ProjectGateway;
import fr.limayrac.demo.task.application.port.out.TaskPort;
import fr.limayrac.demo.task.domain.model.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskUseCase {

    private final TaskPort taskPort;
    private final ProjectGateway projectGateway;

    @Override
    public Task createTask(Long projectId, Task task, String tenantId) {
        if (!projectGateway.existsById(projectId, tenantId)) {
            throw new IllegalArgumentException("Project not found with id: " + projectId + " for tenant: " + tenantId);
        }
        task.setProjectId(projectId);
        task.setTenantId(tenantId);
        if (task.getCreatedOn() == null) {
            task.setCreatedOn(LocalDateTime.now());
        }
        return taskPort.save(task);
    }

    @Override
    public Task updateTask(Long id, Task taskUpdates, String tenantId) {
        Task existingTask = taskPort.findById(id, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + id));
        
        // Update relevant fields
        if (taskUpdates.getTitle() != null) existingTask.setTitle(taskUpdates.getTitle());
        if (taskUpdates.getDescription() != null) existingTask.setDescription(taskUpdates.getDescription());
        if (taskUpdates.getDueDate() != null) existingTask.setDueDate(taskUpdates.getDueDate());
        // Boolean fields are tricky with null check, assume explicit set if calling update
        // But for patch, maybe separate method. Here implementing simple update.
        // Let's assume passed object has new state.
        existingTask.setCompleted(taskUpdates.isCompleted());
        
        return taskPort.save(existingTask);
    }

    @Override
    public void deleteTask(Long id, String tenantId) {
        if (taskPort.findById(id, tenantId).isPresent()) {
             taskPort.deleteById(id, tenantId);
        } else {
             throw new IllegalArgumentException("Task not found with id: " + id);
        }
    }

    @Override
    public Optional<Task> getTaskById(Long id, String tenantId) {
        return taskPort.findById(id, tenantId);
    }

    @Override
    public List<Task> getAllTasks(String tenantId) {
        return taskPort.findAll(tenantId);
    }

    @Override
    public Page<Task> getTasksByProjectId(Long projectId, Pageable pageable, String tenantId) {
        return taskPort.findByProjectId(projectId, pageable, tenantId);
    }

    @Override
    public long count(String tenantId) {
        return taskPort.count(tenantId);
    }

    @Override
    public long countOverdue(String tenantId) {
        return taskPort.countOverdue(tenantId);
    }
}
