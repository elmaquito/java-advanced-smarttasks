package fr.limayrac.demo.project.infrastructure.web.dto;

import java.time.LocalDateTime;

public record ProjectResponse(
    Long id,
    String name,
    LocalDateTime createdOn
) {}
