package fr.limayrac.demo.project.application.service;

import fr.limayrac.demo.project.application.port.in.ProjectUseCase;
import fr.limayrac.demo.project.application.port.out.ProjectPort;
import fr.limayrac.demo.project.domain.model.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectUseCase {

    private final ProjectPort projectPort;

    @Override
    public Project create(String name, String tenantId) {
        Project project = Project.builder()
                .name(name)
                .tenantId(tenantId)
                .build();
        return projectPort.save(project);
    }

    @Override
    public Project getById(Long id, String tenantId) {
        return projectPort.findById(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
    }

    @Override
    public List<Project> getAll(String tenantId) {
        return projectPort.findAll(tenantId);
    }

    @Override
    public Page<Project> getAll(Pageable pageable, String tenantId) {
        return projectPort.findAll(pageable, tenantId);
    }

    @Override
    public Project update(Long id, String name, String tenantId) {
        Project project = getById(id, tenantId);
        project.setName(name);
        return projectPort.save(project);
    }

    @Override
    public void delete(Long id, String tenantId) {
        projectPort.delete(id, tenantId);
    }

    @Override
    public long count(String tenantId) {
        return projectPort.count(tenantId);
    }

    @Override
    public List<Project> getLatest(int limit, String tenantId) {
        return projectPort.getLatest(limit, tenantId);
    }
}
