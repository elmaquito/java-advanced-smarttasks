package fr.limayrac.demo.project.infrastructure.web.mapper;

import fr.limayrac.demo.project.domain.model.Project;
import fr.limayrac.demo.project.infrastructure.web.dto.ProjectResponse;
import org.springframework.stereotype.Component;

@Component
public class ProjectDtoMapper {

    public ProjectResponse toResponse(Project project) {
        if (project == null) {
            return null;
        }
        return new ProjectResponse(
            project.getId(),
            project.getName(),
            project.getCreatedOn()
        );
    }
}
