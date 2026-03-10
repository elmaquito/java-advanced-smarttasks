package fr.limayrac.demo.project.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record ProjectResponse(
    Long id,
    String name,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime createdOn
) {}
