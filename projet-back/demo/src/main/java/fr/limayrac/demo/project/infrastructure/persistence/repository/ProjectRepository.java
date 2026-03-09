package fr.limayrac.demo.project.infrastructure.persistence.repository;

import fr.limayrac.demo.project.infrastructure.persistence.entity.ProjectEntity;
import fr.limayrac.demo.dto.ProjectListResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {

    Optional<ProjectEntity> findByIdAndTenantId(Long id, String tenantId);
    Page<ProjectEntity> findAllByTenantId(String tenantId, Pageable pageable);

    @Query("""
           SELECT new fr.limayrac.demo.dto.ProjectListResponse(
               p.id,
               p.name,
               p.createdOn
           )
           FROM ProjectEntity p
           WHERE p.tenantId = :tenantId
           """)
    List<ProjectListResponse> findAllProjectSummaries(String tenantId);

    long countByTenantId(String tenantId);
    
    @Query("""
           SELECT new fr.limayrac.demo.dto.ProjectListResponse(
               p.id,
               p.name,
               p.createdOn
           )
           FROM ProjectEntity p
           WHERE p.tenantId = :tenantId
           ORDER BY p.createdOn DESC
           """)
    List<ProjectListResponse> findLatest(String tenantId, Pageable pageable);
}
