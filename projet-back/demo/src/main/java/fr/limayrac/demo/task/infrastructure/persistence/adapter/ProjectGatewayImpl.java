package fr.limayrac.demo.task.infrastructure.persistence.adapter;

import fr.limayrac.demo.project.application.port.in.ProjectUseCase;
import fr.limayrac.demo.task.application.port.out.ProjectGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectGatewayImpl implements ProjectGateway {

    private final ProjectUseCase projectUseCase;

    @Override
    public boolean existsById(Long projectId, String tenantId) {
        try {
            projectUseCase.getById(projectId, tenantId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
