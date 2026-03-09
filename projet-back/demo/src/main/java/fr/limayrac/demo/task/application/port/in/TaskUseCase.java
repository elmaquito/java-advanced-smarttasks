package fr.limayrac.demo.task.application.port.in;

import fr.limayrac.demo.task.domain.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface TaskUseCase {
    Task createTask(Long projectId, Task task, String tenantId);
    Task updateTask(Long id, Task task, String tenantId);
    void deleteTask(Long id, String tenantId);
    Optional<Task> getTaskById(Long id, String tenantId);
    List<Task> getAllTasks(String tenantId);
    Page<Task> getTasksByProjectId(Long projectId, Pageable pageable, String tenantId);
    long count(String tenantId);
    long countOverdue(String tenantId);
}
