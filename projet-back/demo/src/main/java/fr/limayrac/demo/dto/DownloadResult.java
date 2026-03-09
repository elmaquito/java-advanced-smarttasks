package fr.limayrac.demo.dto;

import org.springframework.core.io.InputStreamResource;

public record DownloadResult(
    InputStreamResource resource,
    String fileName,
    String mimeType,
    long size
) {}
