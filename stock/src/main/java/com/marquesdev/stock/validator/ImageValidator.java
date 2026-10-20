package com.marquesdev.stock.validator;

import com.marquesdev.stock.exception.InvalidImageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
public class ImageValidator {

    private final DataSize maxSize;
    private final List<String> allowedContentTypes;

    public ImageValidator(
            @Value("${app.upload.max-size}") DataSize maxSize,
            @Value("${app.upload.allowed-content-types}") List<String> allowedContentTypes
    ) {
        this.maxSize = maxSize;
        this.allowedContentTypes = List.copyOf(allowedContentTypes);
    }

    public void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidImageException("Image must not be empty.");
        }
        if (file.getSize() > maxSize.toBytes()) {
            throw new InvalidImageException("Image must not exceed " + maxSize + ".");
        }
        if (file.getContentType() == null || !allowedContentTypes.contains(file.getContentType())) {
            throw new InvalidImageException("Unsupported image format.");
        }
    }
}
