package fr.limayrac.demo.controller;

import fr.limayrac.demo.dto.AttachmentResponse;
import fr.limayrac.demo.dto.DownloadResult;
import fr.limayrac.demo.service.AttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Attachments", description = "Gestion des pièces jointes des tâches")
public class AttachmentController {

    private final AttachmentService attachmentService;

    @Operation(summary = "Lister les pièces jointes d'une tâche", description = "Retourne la liste des fichiers attachés à une tâche avec pagination")
    @ApiResponse(responseCode = "200", description = "Liste récupérée")
    @GetMapping("/{taskId}/attachments")
    public Page<AttachmentResponse> findAll(@PathVariable Long taskId, Pageable pageable) {
        return attachmentService.findAllByTaskId(taskId, pageable);
    }

    @Operation(summary = "Téléverser une pièce jointe", description = "Ajoute un fichier à une tâche spécifique")
    @ApiResponse(responseCode = "200", description = "Fichier téléversé avec succès", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AttachmentResponse.class)))
    @ApiResponse(responseCode = "404", description = "Tâche non trouvée")
    @PostMapping(value = "/{taskId}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AttachmentResponse upload(@PathVariable Long taskId, @RequestParam("file") MultipartFile file) {
        return attachmentService.create(taskId, file);
    }

    @Operation(summary = "Télécharger une pièce jointe", description = "Télécharge le fichier associé à l'ID de pièce jointe fourni")
    @ApiResponse(responseCode = "200", description = "Fichier téléchargé avec succès")
    @ApiResponse(responseCode = "404", description = "Pièce jointe non trouvée")
    @GetMapping("/{id}/download")
    public ResponseEntity<InputStreamResource> downloadAttachment(@PathVariable Long id) {
        DownloadResult result = attachmentService.download(id);

        String encodedName = URLEncoder.encode(result.fileName(), StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedName + "\"")
                .header(HttpHeaders.CONTENT_TYPE, result.mimeType())
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(result.size()))
                .body(result.resource());
    }
}
