package com.marquesdev.stock.service;

import com.marquesdev.stock.validator.ImageValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {

    private static final Set<String> ALLOWED_DIRECTORIES = Set.of("categories", "products");

    private final ImageValidator imageValidator;

    @Value("${app.upload.images}")
    private String uploadDirectory;

    public String store(MultipartFile file, String directoryName){
        imageValidator.validate(file);

        Path directory = resolverDirectory(directoryName);
        String extension = getExtension(file.getContentType());
        String filename = UUID.randomUUID() + extension;
        Path destination = directory.resolve(filename);

        try {
            Files.createDirectories(directory);
            file.transferTo(destination);
        }catch (IOException exception){
            throw new IllegalStateException(
                    "Failed to store image.", exception
            );
        }

        return directoryName + "/" + filename;
    }

    public String replace(String oldImageKey, MultipartFile file, String directoryName) {
        String newImageKey = store(file, directoryName);
        delete(oldImageKey);
        return newImageKey;
    }

    public void delete(String imageKey){
        if (imageKey == null || imageKey.isBlank()){
            return;
        }
        Path root = Paths.get(uploadDirectory)
                .toAbsolutePath()
                .normalize();

        Path imagePath = root.resolve(imageKey).normalize();

        if (!imagePath.startsWith(root)){
            throw new IllegalArgumentException(
                    "Invalid image path.");
        }

        try {
            Files.deleteIfExists(imagePath);
        }catch (IOException exception){
            throw new IllegalStateException(
                    "Failed to delete image.", exception
            );
        }
    }

    private Path resolverDirectory(String directoryName){
        if (!ALLOWED_DIRECTORIES.contains(directoryName)){
            throw new IllegalArgumentException(
                    "Unsupported image directory"
            );
        }
        return Paths.get(uploadDirectory)
                .toAbsolutePath()
                .normalize()
                .resolve(directoryName)
                .normalize();
    }

    private String getExtension(String contentType){
        return switch (contentType){
            case "image/jpeg" -> ".jpeg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> throw new IllegalArgumentException("Unsupported image format.");
        };
    }
}
