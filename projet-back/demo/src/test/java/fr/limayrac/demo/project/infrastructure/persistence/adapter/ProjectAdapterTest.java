package fr.limayrac.demo.project.infrastructure.persistence.adapter;

import fr.limayrac.demo.project.domain.model.Project;
import fr.limayrac.demo.project.infrastructure.persistence.entity.ProjectEntity;
import fr.limayrac.demo.project.infrastructure.persistence.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectAdapterTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectAdapter projectAdapter;

    private ProjectEntity projectEntity;
    private Project project;

    @BeforeEach
    void setUp() {
        projectEntity = new ProjectEntity();
        projectEntity.setId(1L);
        projectEntity.setName("Test Project");
        projectEntity.setTenantId("tenant-1");
        projectEntity.setCreatedOn(LocalDateTime.now());

        project = Project.builder()
                .id(1L)
                .name("Test Project")
                .tenantId("tenant-1")
                .createdOn(projectEntity.getCreatedOn())
                .build();
    }

    @Test
    void findAll_ShouldReturnListOfProjects() {
        when(projectRepository.findAllByTenantId(eq("tenant-1"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(projectEntity)));

        List<Project> result = projectAdapter.findAll("tenant-1");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Test Project", result.get(0).getName());
    }

    @Test
    void findById_ShouldReturnProject() {
        when(projectRepository.findByIdAndTenantId(1L, "tenant-1"))
                .thenReturn(Optional.of(projectEntity));

        Optional<Project> result = projectAdapter.findById(1L, "tenant-1");

        assertTrue(result.isPresent());
        assertEquals("Test Project", result.get().getName());
    }

    @Test
    void save_ShouldReturnSavedProject() {
        when(projectRepository.save(any(ProjectEntity.class))).thenReturn(projectEntity);

        Project result = projectAdapter.save(project);

        assertNotNull(result);
        assertEquals("Test Project", result.getName());
    }

    @Test
    void delete_ShouldCallDelete() {
        when(projectRepository.findByIdAndTenantId(1L, "tenant-1"))
                .thenReturn(Optional.of(projectEntity));
        doNothing().when(projectRepository).delete(projectEntity);

        projectAdapter.delete(1L, "tenant-1");

        verify(projectRepository, times(1)).delete(projectEntity);
    }
}
