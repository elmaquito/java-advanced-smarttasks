package fr.limayrac.demo.dto;

import java.time.LocalDateTime;

public record ProjectListResponse(
    Long id,
    String name,
    LocalDateTime createdOn
) {}
