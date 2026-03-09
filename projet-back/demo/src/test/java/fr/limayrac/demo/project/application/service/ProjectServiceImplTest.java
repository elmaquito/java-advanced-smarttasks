package fr.limayrac.demo.project.application.service;

import fr.limayrac.demo.project.application.port.out.ProjectPort;
import fr.limayrac.demo.project.domain.model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectPort projectPort;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private Project project;

    @BeforeEach
    void setUp() {
        project = Project.builder()
                .id(1L)
                .name("Test Project")
                .tenantId("tenant-1")
                .createdOn(LocalDateTime.now())
                .build();
    }

    @Test
    void create_ShouldReturnProject() {
        when(projectPort.save(any(Project.class))).thenReturn(project);

        Project result = projectService.create("Test Project", "tenant-1");

        assertNotNull(result);
        assertEquals("Test Project", result.getName());
        verify(projectPort, times(1)).save(any(Project.class));
    }

    @Test
    void getById_ShouldReturnProject() {
        when(projectPort.findById(1L, "tenant-1")).thenReturn(Optional.of(project));

        Project result = projectService.getById(1L, "tenant-1");

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(projectPort, times(1)).findById(1L, "tenant-1");
    }

    @Test
    void getAll_ShouldReturnListOfProjects() {
        when(projectPort.findAll("tenant-1")).thenReturn(Collections.singletonList(project));

        List<Project> result = projectService.getAll("tenant-1");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(projectPort, times(1)).findAll("tenant-1");
    }

    @Test
    void update_ShouldUpdateAndReturnProject() {
        Project updatedProject = Project.builder()
                .id(1L)
                .name("Updated Name")
                .tenantId("tenant-1")
                .createdOn(project.getCreatedOn())
                .build();
        
        when(projectPort.findById(1L, "tenant-1")).thenReturn(Optional.of(project));
        when(projectPort.save(any(Project.class))).thenReturn(updatedProject);

        Project result = projectService.update(1L, "Updated Name", "tenant-1");

        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        verify(projectPort, times(1)).findById(1L, "tenant-1");
        verify(projectPort, times(1)).save(any(Project.class));
    }

    @Test
    void delete_ShouldCallDelete() {
        doNothing().when(projectPort).delete(1L, "tenant-1");
        projectService.delete(1L, "tenant-1");
        verify(projectPort, times(1)).delete(1L, "tenant-1");
    }
}
