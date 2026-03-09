package fr.limayrac.demo.domain;

import fr.limayrac.demo.task.infrastructure.persistence.entity.TaskEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "attachments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private String tenantId;

    @Column(nullable = false)
    private String objectKey;

    @Column(nullable = false)
    private String originalName;

    private String mimeType;

    private Long size;

    @Column(updatable = false, nullable = false)
    private LocalDateTime uploadedOn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private TaskEntity task;

    @PrePersist
    protected void onUpload() {
        this.uploadedOn = LocalDateTime.now();
    }
}
