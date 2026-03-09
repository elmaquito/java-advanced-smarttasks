package fr.limayrac.demo.task.application.port.out;

import java.util.Optional;

public interface ProjectGateway {
    boolean existsById(Long projectId, String tenantId);
}
