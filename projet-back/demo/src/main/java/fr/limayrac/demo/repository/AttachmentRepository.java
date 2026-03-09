package fr.limayrac.demo.repository;

import fr.limayrac.demo.domain.Attachment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    @Query("SELECT a FROM Attachment a WHERE a.tenantId = :tenantId")
    Page<Attachment> findAllByTenantId(@Param("tenantId") String tenantId, Pageable pageable);

    Page<Attachment> findAllByTaskIdAndTenantId(Long taskId, String tenantId, Pageable pageable);

    Optional<Attachment> findByIdAndTenantId(Long id, String tenantId);
}
