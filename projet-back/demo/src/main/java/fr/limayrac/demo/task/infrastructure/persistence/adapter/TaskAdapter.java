package fr.limayrac.demo.task.infrastructure.persistence.adapter;

import fr.limayrac.demo.task.application.port.out.TaskPort;
import fr.limayrac.demo.task.domain.model.Task;
import fr.limayrac.demo.task.infrastructure.persistence.entity.TaskEntity;
import fr.limayrac.demo.task.infrastructure.persistence.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TaskAdapter implements TaskPort {

    private final TaskRepository taskRepository;

    @Override
    public Task save(Task task) {
        TaskEntity entity = toEntity(task);
        if(task.getId() == null && task.getCreatedOn() != null) {
            entity.setCreatedOn(task.getCreatedOn());
        }
        TaskEntity saved = taskRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Task> findById(Long id, String tenantId) {
        return taskRepository.findByIdAndTenantId(id, tenantId).map(this::toDomain);
    }

    @Override
    public void deleteById(Long id, String tenantId) {
        taskRepository.deleteById(id);
    }

    @Override
    public List<Task> findAll(String tenantId) {
        return taskRepository.findAllByTenantId(tenantId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Page<Task> findByProjectId(Long projectId, Pageable pageable, String tenantId) {
        return taskRepository.findAllByProjectIdAndTenantId(projectId, tenantId, pageable)
                .map(this::toDomain);
    }

    private Task toDomain(TaskEntity entity) {
        return new Task(
                entity.getId(),
                entity.getTenantId(),
                entity.getProjectId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getDueDate(),
                entity.isCompleted(),
                entity.getCreatedOn()
        );
    }

    private TaskEntity toEntity(Task domain) {
        TaskEntity entity = new TaskEntity();
        entity.setId(domain.getId());
        entity.setProjectId(domain.getProjectId());
        entity.setTenantId(domain.getTenantId());
        entity.setTitle(domain.getTitle());
        entity.setDescription(domain.getDescription());
        entity.setDueDate(domain.getDueDate());
        entity.setCompleted(domain.isCompleted());
        if(domain.getCreatedOn() != null) {
            entity.setCreatedOn(domain.getCreatedOn());
        }
        return entity;
    }
}
