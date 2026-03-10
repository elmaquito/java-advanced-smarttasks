package fr.limayrac.demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

public record TaskListResponse(
    Long id,
    String title,
    boolean completed,
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate dueDate
) {}
