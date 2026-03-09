package fr.limayrac.demo.service;

import fr.limayrac.demo.configuration.tenant.TenantContext;
import fr.limayrac.demo.domain.Attachment;
import fr.limayrac.demo.task.infrastructure.persistence.entity.TaskEntity;
import fr.limayrac.demo.dto.AttachmentResponse;
import fr.limayrac.demo.repository.AttachmentRepository;
import fr.limayrac.demo.task.infrastructure.persistence.repository.TaskRepository;
import fr.limayrac.demo.service.minio.MinioService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttachmentServiceTest {

    @Mock
    private AttachmentRepository attachmentRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private MinioService minioService;

    @InjectMocks
    private AttachmentService attachmentService;

    @BeforeEach
    void setUp() {
        TenantContext.setTenant("default");
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void create_ShouldUploadFileAndSaveAttachment() throws Exception {
        // Given
        Long taskId = 1L;
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "content".getBytes()
        );

        TaskEntity task = new TaskEntity();
        task.setId(taskId);
        task.setTenantId("default");

        when(taskRepository.findByIdAndTenantId(taskId, "default")).thenReturn(Optional.of(task));
        when(minioService.uploadFile(any())).thenReturn("bucket/test-key");
        
        // Mock save
        when(attachmentRepository.save(any(Attachment.class))).thenAnswer(invocation -> {
            Attachment a = invocation.getArgument(0);
            a.setId(100L); // simulate saved id
            a.setUploadedOn(LocalDateTime.now());
            return a;
        });

        // When
        AttachmentResponse response = attachmentService.create(taskId, file);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.fileName()).isEqualTo("test.txt");
        assertThat(response.id()).isEqualTo(100L);

        verify(taskRepository).findByIdAndTenantId(taskId, "default");
        verify(minioService).uploadFile(any());
        verify(attachmentRepository).save(any(Attachment.class));
    }
}
