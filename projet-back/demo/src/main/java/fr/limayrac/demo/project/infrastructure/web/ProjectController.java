package fr.limayrac.demo.project.infrastructure.web;

import fr.limayrac.demo.project.application.port.in.ProjectUseCase;
import fr.limayrac.demo.project.domain.model.Project;
import fr.limayrac.demo.project.infrastructure.web.dto.ProjectCreateRequest;
import fr.limayrac.demo.project.infrastructure.web.dto.ProjectResponse;
import fr.limayrac.demo.project.infrastructure.web.mapper.ProjectDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Tag(name = "Projects", description = "Gestion des projets")
public class ProjectController {

    private final ProjectUseCase projectUseCase;
    private final ProjectDtoMapper projectDtoMapper;

    @Operation(summary = "Lister les projets", description = "Retourne tous les projets associés au tenant courant")
    @ApiResponse(responseCode = "200", description = "Succès")
    @GetMapping
    public List<ProjectResponse> findAll(@RequestHeader(value = "X-Tenant-ID", defaultValue = "default") String tenantId) {
        return projectUseCase.getAll(tenantId).stream()
                .map(projectDtoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Créer un projet", description = "Crée un nouveau projet.")
    @ApiResponse(responseCode = "200", description = "Projet créé")
    @PostMapping
    public ProjectResponse create(@RequestHeader(value = "X-Tenant-ID", defaultValue = "default") String tenantId,
                                  @RequestBody ProjectCreateRequest request) {
        Project createdProject = projectUseCase.create(request.getName(), tenantId);
        return projectDtoMapper.toResponse(createdProject);
    }
}
