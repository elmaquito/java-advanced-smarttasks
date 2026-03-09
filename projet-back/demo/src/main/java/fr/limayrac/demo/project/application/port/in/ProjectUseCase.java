package fr.limayrac.demo.project.application.port.in;

import fr.limayrac.demo.project.domain.model.Project;
import java.util.List;

public interface ProjectUseCase {
    Project create(String name, String tenantId);
    Project getById(Long id, String tenantId);
    List<Project> getAll(String tenantId);
    Project update(Long id, String name, String tenantId);
    void delete(Long id, String tenantId);
    long count(String tenantId);
    List<Project> getLatest(int limit, String tenantId);
}
