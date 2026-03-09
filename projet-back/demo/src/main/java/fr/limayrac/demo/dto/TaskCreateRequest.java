package fr.limayrac.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record TaskCreateRequest(
        @NotBlank(message = "Title is mandatory")
        @Size(max = 100, message = "Title must not exceed 100 characters")
        String title,
        String description,
        LocalDate dueDate
) {}
