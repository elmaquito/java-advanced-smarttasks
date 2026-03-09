package fr.limayrac.demo.task.application.port.out;

import fr.limayrac.demo.task.domain.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface TaskPort {
    Task save(Task task);
    Optional<Task> findById(Long id, String tenantId);
    void deleteById(Long id, String tenantId);
    List<Task> findAll(String tenantId);
    Page<Task> findByProjectId(Long projectId, Pageable pageable, String tenantId);
    long count(String tenantId);
    long countOverdue(String tenantId);
}
