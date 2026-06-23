package com.royalhouse.cms.admin.common.validation;

import org.springframework.web.multipart.MultipartFile;

public interface ImageFileValidator {
    void validateImage(MultipartFile file);
}
