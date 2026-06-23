package com.royalhouse.cms.admin.common.validation;

import com.royalhouse.cms.core.common.exception.BusinessValidationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;
import java.util.Set;

@Service
public class DefaultImageFileValidator implements ImageFileValidator {

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    @Override
    public void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return;
        }

        String contentType = file.getContentType();

        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new BusinessValidationException("Файл должен быть изображением JPG, PNG или WEBP");
        }
    }
}
