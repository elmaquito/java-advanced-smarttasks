package fr.limayrac.demo.controller;

import fr.limayrac.demo.configuration.tenant.TenantContext;
import fr.limayrac.demo.dto.DashboardResponse;
import fr.limayrac.demo.dto.ProjectListResponse;
import fr.limayrac.demo.project.application.port.in.ProjectUseCase;
import fr.limayrac.demo.task.application.port.in.TaskUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Operations for dashboard statistics")
public class DashboardController {

    private final ProjectUseCase projectUseCase;
    private final TaskUseCase taskUseCase;

    @GetMapping
    @Operation(summary = "Get dashboard statistics", description = "Returns project and task counts, and latest projects")
    public DashboardResponse getDashboard() {
        String tenantId = TenantContext.getTenant();
        
        long activeProjects = projectUseCase.count(tenantId);
        long totalTasks = taskUseCase.count(tenantId);
        long overdueTasks = taskUseCase.countOverdue(tenantId);
        
        List<ProjectListResponse> latestProjects = projectUseCase.getLatest(5, tenantId).stream()
            .map(p -> new ProjectListResponse(p.getId(), p.getName(), p.getCreatedOn()))
            .collect(Collectors.toList());

        return new DashboardResponse(activeProjects, totalTasks, overdueTasks, latestProjects);
    }
}
