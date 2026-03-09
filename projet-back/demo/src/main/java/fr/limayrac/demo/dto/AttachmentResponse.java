package fr.limayrac.demo.dto;

import java.time.LocalDateTime;

public record AttachmentResponse(
    Long id,
    String fileName,
    String mimeType,
    Long size,
    LocalDateTime uploadedOn,
    String downloadUrl
) {}
