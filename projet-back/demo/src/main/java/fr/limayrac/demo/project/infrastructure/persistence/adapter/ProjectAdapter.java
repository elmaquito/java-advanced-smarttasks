package fr.limayrac.demo.project.infrastructure.persistence.adapter;

import fr.limayrac.demo.project.application.port.out.ProjectPort;
import fr.limayrac.demo.project.domain.model.Project;
import fr.limayrac.demo.project.infrastructure.persistence.entity.ProjectEntity;
import fr.limayrac.demo.project.infrastructure.persistence.repository.ProjectRepository;
import fr.limayrac.demo.dto.ProjectListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProjectAdapter implements ProjectPort {

    private final ProjectRepository projectRepository;

    @Override
    public List<Project> findAll(String tenantId) {
        return projectRepository.findAllByTenantId(tenantId, org.springframework.data.domain.Pageable.unpaged())
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Project> findById(Long id, String tenantId) {
        return projectRepository.findByIdAndTenantId(id, tenantId)
                .map(this::toDomain);
    }

    @Override
    public Project save(Project project) {
        ProjectEntity entity = toEntity(project);
        ProjectEntity saved = projectRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public void delete(Long id, String tenantId) {
        projectRepository.findByIdAndTenantId(id, tenantId)
                .ifPresent(projectRepository::delete);
    }

    private Project toDomain(ProjectEntity entity) {
        return Project.builder()
                .id(entity.getId())
                .name(entity.getName())
                .tenantId(entity.getTenantId())
                .createdOn(entity.getCreatedOn())
                .build();
    }

    private ProjectEntity toEntity(Project domain) {
        ProjectEntity entity = new ProjectEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setTenantId(domain.getTenantId());
        entity.setCreatedOn(domain.getCreatedOn());
        return entity;
    }
}
