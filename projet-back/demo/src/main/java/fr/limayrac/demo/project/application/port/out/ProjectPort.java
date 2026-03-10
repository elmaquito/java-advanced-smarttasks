package fr.limayrac.demo.project.application.port.out;

import fr.limayrac.demo.project.domain.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface ProjectPort {
    List<Project> findAll(String tenantId);
    Page<Project> findAll(Pageable pageable, String tenantId);
    Optional<Project> findById(Long id, String tenantId);
    Project save(Project project);
    void delete(Long id, String tenantId);
    long count(String tenantId);
    List<Project> getLatest(int limit, String tenantId);
}
