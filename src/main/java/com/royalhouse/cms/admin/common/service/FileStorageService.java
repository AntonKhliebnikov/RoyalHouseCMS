package com.royalhouse.cms.admin.common.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String store(MultipartFile file, String relativeDir);

    void delete(String publicPath);
}
