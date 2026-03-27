package com.royalhouse.cms.admin.common.service;


import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

@Service
@Log4j2
public class LocalFileStorageService implements FileStorageService {
    private final Path rootPath;

    public LocalFileStorageService(@Value("${app.uploads.dir}") String uploadsDir) {
        try {
            this.rootPath = Paths.get(uploadsDir).toAbsolutePath().normalize();
            Files.createDirectories(this.rootPath);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to initialize download directory", e);
        }
    }

    @Override
    public String store(MultipartFile file, String relativeDir) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            String extension = StringUtils.getFilenameExtension(originalFilename);

            String fileName = UUID.randomUUID() + (extension != null && !extension.isBlank() ? "." + extension : "");
            Path targetDir = rootPath.resolve(relativeDir).normalize();
            Files.createDirectories(targetDir);

            Path targetFile = targetDir.resolve(fileName).normalize();

            if (!targetFile.startsWith(rootPath)) {
                throw new IllegalStateException("Incorrect path to save file");
            }

            file.transferTo(targetFile);
            String normalizedRelativeDir = relativeDir.replace("\\", "/");
            return "/uploads/" + normalizedRelativeDir + "/" + fileName;

        } catch (IOException e) {
            throw new IllegalStateException("Failed to save file:" + file.getOriginalFilename(), e);
        }
    }

    @Override
    public void delete(String publicPath) {
        if (!StringUtils.hasText(publicPath)) {
            return;
        }

        try {
            String relativePath = publicPath.startsWith("/uploads/") ? publicPath.substring("/uploads/".length()) : publicPath;
            Path filePath = rootPath.resolve(relativePath).normalize();

            if (filePath.startsWith(rootPath)) {
                Files.deleteIfExists(filePath);
            }
        } catch (IOException e) {
            log.warn("Failed to delete file {}", publicPath, e);
        }
    }
}