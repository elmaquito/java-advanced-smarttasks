package fr.limayrac.demo.dto;

import java.time.LocalDate;

public record TaskListResponse(
    Long id,
    String title,
    boolean completed,
    LocalDate dueDate
) {}
