package fr.limayrac.demo.task.application.service;

import fr.limayrac.demo.task.application.port.out.ProjectGateway;
import fr.limayrac.demo.task.application.port.out.TaskPort;
import fr.limayrac.demo.task.domain.model.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskPort taskPort;

    @Mock
    private ProjectGateway projectGateway;

    @InjectMocks
    private TaskServiceImpl taskService;

    @Test
    void createTask_ShouldValidateProjectAndSave() {
        // Given
        Long projectId = 1L;
        String tenantId = "default";
        Task task = new Task();
        task.setTitle("New Task");

        when(projectGateway.existsById(projectId, tenantId)).thenReturn(true);
        when(taskPort.save(any(Task.class))).thenAnswer(invocation -> {
            Task t = invocation.getArgument(0);
            t.setId(10L);
            return t;
        });

        // When
        Task created = taskService.createTask(projectId, task, tenantId);

        // Then
        assertThat(created.getId()).isEqualTo(10L);
        assertThat(created.getProjectId()).isEqualTo(projectId);
        assertThat(created.getTenantId()).isEqualTo(tenantId);
        verify(projectGateway).existsById(projectId, tenantId);
        verify(taskPort).save(any(Task.class));
    }
}
