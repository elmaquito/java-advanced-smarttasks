package fr.limayrac.demo.service;

import fr.limayrac.demo.configuration.tenant.TenantContext;
import fr.limayrac.demo.domain.Attachment;
import fr.limayrac.demo.task.infrastructure.persistence.entity.TaskEntity;
import fr.limayrac.demo.dto.AttachmentResponse;
import fr.limayrac.demo.dto.DownloadResult;
import fr.limayrac.demo.repository.AttachmentRepository;
import fr.limayrac.demo.task.infrastructure.persistence.repository.TaskRepository;
import fr.limayrac.demo.service.minio.MinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final TaskRepository taskRepository;
    private final MinioService minioService;

    @Transactional
    public AttachmentResponse create(Long taskId, MultipartFile file) {
        TaskEntity task = taskRepository.findByIdAndTenantId(taskId, TenantContext.getTenant())
                .orElseThrow(() -> new RuntimeException("Task not found"));

        try {
            String objectKey = minioService.uploadFile(file);

            Attachment attachment = new Attachment();
            attachment.setTenantId(TenantContext.getTenant());
            attachment.setTask(task);
            attachment.setObjectKey(objectKey);
            attachment.setOriginalName(file.getOriginalFilename());
            attachment.setMimeType(file.getContentType());
            attachment.setSize(file.getSize());

            Attachment saved = attachmentRepository.save(attachment);
            return mapToResponse(saved);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'upload", e);
        }
    }

    public Page<AttachmentResponse> findAllByTaskId(Long taskId, Pageable pageable) {
        return attachmentRepository.findAllByTaskIdAndTenantId(taskId, TenantContext.getTenant(), pageable)
                .map(this::mapToResponse);
    }

    public DownloadResult download(Long id) {
        Attachment attachment = attachmentRepository.findByIdAndTenantId(id, TenantContext.getTenant())
                .orElseThrow(() -> new RuntimeException("Attachment not found"));

        try {
            var stream = minioService.downloadFile(attachment.getObjectKey());
            return new DownloadResult(
                    new InputStreamResource(stream),
                    attachment.getOriginalName(),
                    attachment.getMimeType(),
                    attachment.getSize()
            );
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du téléchargement", e);
        }
    }

    private AttachmentResponse mapToResponse(Attachment attachment) {
        return new AttachmentResponse(
                attachment.getId(),
                attachment.getOriginalName(),
                attachment.getMimeType(),
                attachment.getSize(),
                attachment.getUploadedOn(),
                "/api/tasks/" + attachment.getId() + "/download"
        );
    }
}
