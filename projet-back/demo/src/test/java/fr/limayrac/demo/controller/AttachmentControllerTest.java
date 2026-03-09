package fr.limayrac.demo.controller;

import fr.limayrac.demo.dto.AttachmentResponse;
import fr.limayrac.demo.dto.DownloadResult;
import fr.limayrac.demo.service.AttachmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AttachmentController.class)
@WithMockUser
class AttachmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AttachmentService attachmentService;

    @Test
    void upload_ShouldReturnAttachmentResponse() throws Exception {
        Long taskId = 1L;
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello World".getBytes()
        );

        AttachmentResponse response = new AttachmentResponse(
                100L,
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                11L,
                LocalDateTime.now(),
                "/api/tasks/100/download"
        );

        when(attachmentService.create(eq(taskId), any())).thenReturn(response);

        mockMvc.perform(multipart("/api/tasks/{taskId}/attachments", taskId)
                        .file(file)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                            "id": 100,
                            "fileName": "test.txt",
                            "mimeType": "text/plain",
                            "size": 11,
                            "downloadUrl": "/api/tasks/100/download"
                        }
                        """));
    }

    @Test
    void list_ShouldReturnPageOfAttachments() throws Exception {
        Long taskId = 1L;
        AttachmentResponse response = new AttachmentResponse(
                100L,
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                11L,
                LocalDateTime.now(),
                "/api/tasks/100/download"
        );

        Page<AttachmentResponse> page = new PageImpl<>(List.of(response));

        when(attachmentService.findAllByTaskId(eq(taskId), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/tasks/{taskId}/attachments", taskId))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                            "content": [
                                {
                                    "id": 100,
                                    "fileName": "test.txt"
                                }
                            ],
                            "totalElements": 1
                        }
                        """));
    }

    @Test
    void download_ShouldStreamFile() throws Exception {
        Long attachmentId = 100L;
        String content = "Hello World";
        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(content.getBytes()));

        DownloadResult result = new DownloadResult(
                resource,
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                (long) content.length()
        );

        when(attachmentService.download(attachmentId)).thenReturn(result);

        mockMvc.perform(get("/api/tasks/{id}/download", attachmentId))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"test.txt\""))
                .andExpect(header().string("Content-Type", "text/plain"))
                .andExpect(content().string("Hello World"));
    }
}
