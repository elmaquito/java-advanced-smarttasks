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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Tag(name = "Projects", description = "Gestion des projets")
public class ProjectController {

    private final ProjectUseCase projectUseCase;
    private final ProjectDtoMapper projectDtoMapper;

    @Operation(summary = "Lister les projets", description = "Retourne tous les projets associés au tenant courant avec pagination")
    @ApiResponse(responseCode = "200", description = "Succès")
    @GetMapping
    public Page<ProjectResponse> findAll(
            @RequestHeader(value = "X-Tenant-ID", defaultValue = "default") String tenantId,
            Pageable pageable
    ) {
        return projectUseCase.getAll(pageable, tenantId)
                .map(projectDtoMapper::toResponse);
    }

    @Operation(summary = "Créer un projet", description = "Crée un nouveau projet.")
    @ApiResponse(responseCode = "200", description = "Projet créé")
    @PostMapping
    public ProjectResponse create(@RequestHeader(value = "X-Tenant-ID", defaultValue = "default") String tenantId,
                                  @RequestBody ProjectCreateRequest request) {
        Project createdProject = projectUseCase.create(request.getName(), tenantId);
        return projectDtoMapper.toResponse(createdProject);
    }

    @Operation(summary = "Récupérer un projet", description = "Retourne un projet par son ID")
    @ApiResponse(responseCode = "200", description = "Projet trouvé")
    @ApiResponse(responseCode = "404", description = "Projet non trouvé")
    @GetMapping("/{id}")
    public ProjectResponse findById(@RequestHeader(value = "X-Tenant-ID", defaultValue = "default") String tenantId,
                                    @PathVariable Long id) {
        try {
            Project project = projectUseCase.getById(id, tenantId);
            return projectDtoMapper.toResponse(project);
        } catch (RuntimeException e) {
             throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Projet non trouvé", e);
        }
    }

    @Operation(summary = "Mettre à jour un projet", description = "Met à jour un projet existant")
    @ApiResponse(responseCode = "200", description = "Projet mis à jour")
    @ApiResponse(responseCode = "404", description = "Projet non trouvé")
    @PutMapping("/{id}")
    public ProjectResponse update(@RequestHeader(value = "X-Tenant-ID", defaultValue = "default") String tenantId,
                                  @PathVariable Long id,
                                  @RequestBody ProjectCreateRequest request) {
        try {
            Project updatedProject = projectUseCase.update(id, request.getName(), tenantId);
            return projectDtoMapper.toResponse(updatedProject);
        } catch (RuntimeException e) {
             throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Projet non trouvé", e);
        }
    }

    @Operation(summary = "Supprimer un projet", description = "Supprime un projet existant")
    @ApiResponse(responseCode = "204", description = "Projet supprimé")
    @ApiResponse(responseCode = "404", description = "Projet non trouvé")
    @DeleteMapping("/{id}")
    public void delete(@RequestHeader(value = "X-Tenant-ID", defaultValue = "default") String tenantId,
                       @PathVariable Long id) {
        projectUseCase.delete(id, tenantId);
    }
}
