package fr.limayrac.demo.task.infrastructure.web.mapper;

import fr.limayrac.demo.dto.TaskResponse;
import fr.limayrac.demo.task.domain.model.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskDtoMapper {

    public TaskResponse toResponse(Task task) {
        if (task == null) {
            return null;
        }
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDueDate(),
                task.isCompleted(),
                task.getProjectId(),
                task.getCreatedOn()
        );
    }
}
