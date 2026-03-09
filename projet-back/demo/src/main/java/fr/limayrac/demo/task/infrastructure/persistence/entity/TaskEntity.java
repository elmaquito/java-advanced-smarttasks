package fr.limayrac.demo.task.infrastructure.persistence.entity;

import fr.limayrac.demo.domain.Attachment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity(name = "TaskEntity")
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private String tenantId;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(length = 100, nullable = false)
    private String title;

    @Lob
    private String description;

    private LocalDate dueDate;

    @Column(nullable = false)
    private boolean completed = false;

    // We temporarily remove the bidirectional relationship or update it manually
    // Since Attachment depends on Task object, and we are removing Task object for Project, 
    // it's complicated.
    // Ideally Attachment should also be refactored to use taskId Only.
    // BUT since we are demonstrating "Clean Arch -> Microservices", let's assume Attachment is part of the same monolith for now or just remove the field here to avoid compilation error if Attachment is not updated.
    // However, Attachment entity has @ManyToOne Task task.
    // For now, I will comment out attachments here and handle Attachment entity later.
    // @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    // private Set<Attachment> attachments;
    
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdOn;

    @PrePersist
    protected void onCreate() {
        this.createdOn = LocalDateTime.now();
    }
}
