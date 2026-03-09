package fr.limayrac.demo.task.infrastructure.persistence.repository;

import fr.limayrac.demo.task.infrastructure.persistence.entity.TaskEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    Optional<TaskEntity> findByIdAndTenantId(Long id, String tenantId);

    List<TaskEntity> findAllByTenantId(String tenantId);

    Page<TaskEntity> findAllByProjectIdAndTenantId(Long projectId, String tenantId, Pageable pageable);

    long countByTenantId(String tenantId);

    long countByTenantIdAndDueDateBeforeAndCompletedFalse(String tenantId, java.time.LocalDate date);
}
