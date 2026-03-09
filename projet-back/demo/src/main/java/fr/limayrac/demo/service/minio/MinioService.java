package fr.limayrac.demo.service.minio;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;
    
    @Value("${minio.bucket-name}")
    private String bucketName;

    /**
     * Upload un fichier vers MinIO.
     * @return L'ID unique (Object Name) généré pour ce fichier.
     */
    public String uploadFile(MultipartFile file) throws Exception {
        // 1. Générer un nom unique pour éviter les collisions (UUID)
        String objectName = UUID.randomUUID().toString();

        // 2. Envoyer le flux (InputStream) à MinIO
        // Astuce : file.getInputStream(), file.getSize(), file.getContentType()
        try (InputStream is = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(is, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        }
        
        return objectName;
    }

    /**
     * Récupère le flux de données d'un fichier.
     */
    public InputStream downloadFile(String objectKey) throws Exception {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectKey)
                        .build()
        );
    }
}
