package fr.limayrac.demo.project.infrastructure.persistence.repository;

import fr.limayrac.demo.project.infrastructure.persistence.entity.ProjectEntity;
import fr.limayrac.demo.dto.ProjectListResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProjectRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    void testFindById_ShouldReturnProject() {
        // Given
        String tenantId = "user-123";
        ProjectEntity project = new ProjectEntity();
        project.setName("Test Project");
        project.setCreatedOn(java.time.LocalDateTime.now());
        project.setTenantId(tenantId);
        
        ProjectEntity savedProject = entityManager.persistFlushFind(project);

        // When
        Optional<ProjectEntity> foundProject = projectRepository.findByIdAndTenantId(savedProject.getId(), tenantId);

        // Then
        assertThat(foundProject).isPresent();
        assertThat(foundProject.get().getName()).isEqualTo("Test Project");
        assertThat(foundProject.get().getCreatedOn()).isNotNull();
    }

    @Test
    void testFindAllProjectSummaries_ShouldReturnDTOs() {
        // Given
        String tenantId = "user-123";
        ProjectEntity p1 = new ProjectEntity();
        p1.setName("Project 1");
        p1.setCreatedOn(java.time.LocalDateTime.now());
        p1.setTenantId(tenantId);
        entityManager.persist(p1);

        ProjectEntity p2 = new ProjectEntity();
        p2.setName("Project 2");
        p2.setCreatedOn(java.time.LocalDateTime.now());
        p2.setTenantId("other-user");
        entityManager.persist(p2);

        // When
        List<ProjectListResponse> summaries = projectRepository.findAllProjectSummaries(tenantId);

        // Then
        assertThat(summaries).hasSize(1);
        assertThat(summaries.get(0).id()).isEqualTo(p1.getId());
    }
}
