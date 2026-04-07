package com.royalhouse.cms.admin.serviceitem.service;

import com.royalhouse.cms.admin.common.service.FileStorageService;
import com.royalhouse.cms.admin.serviceitem.dto.AdminServiceItemFilterForm;
import com.royalhouse.cms.admin.serviceitem.dto.AdminServiceItemCreateOrUpdateForm;
import com.royalhouse.cms.core.serviceitem.entity.ServiceItem;
import com.royalhouse.cms.core.serviceitem.exception.ServiceItemNotFoundException;
import com.royalhouse.cms.core.serviceitem.repository.ServiceItemRepository;
import com.royalhouse.cms.core.serviceitem.specification.ServiceItemSpecifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class AdminServiceItemService {
    private final ServiceItemRepository serviceItemRepository;
    private final FileStorageService fileStorageService;


    @Transactional(readOnly = true)
    public Page<ServiceItem> findAll(AdminServiceItemFilterForm filter, Pageable pageable) {
        log.debug("Find all services by filters");

        Specification<ServiceItem> specification = buildSpecification(filter);
        return serviceItemRepository.findAll(specification, pageable);
    }

    @Transactional(readOnly = true)
    public ServiceItem getById(Long id) {
        log.debug("Find service by id={}", id);

        return serviceItemRepository.findById(id).orElseThrow(
                () -> new ServiceItemNotFoundException(id)
        );
    }

    public ServiceItem createServiceItem(AdminServiceItemCreateOrUpdateForm form) {
        log.debug("Create new service");

        ServiceItem serviceItem = new ServiceItem();
        fillCommonFields(form, serviceItem);
        ServiceItem serviceItemWithId = serviceItemRepository.save(serviceItem);

        String bannerImagePath = resolveBannerPath(
                serviceItemWithId.getId(),
                form.getBannerImage(),
                null
        );

        String previewImagePath = resolvePreviewPath(
                serviceItemWithId.getId(),
                form.getPreviewImage(),
                null
        );

        serviceItemWithId.setBannerImagePath(bannerImagePath);
        serviceItemWithId.setPreviewImagePath(previewImagePath);

        return serviceItemRepository.save(serviceItemWithId);
    }

    public ServiceItem update(Long id, AdminServiceItemCreateOrUpdateForm form) {
        log.debug("Update service with id={}", id);

        ServiceItem serviceItem = getById(id);
        fillCommonFields(form, serviceItem);

        String bannerImagePath = resolveBannerPath(
                serviceItem.getId(),
                form.getBannerImage(),
                serviceItem.getBannerImagePath()
        );

        String previewImagePath = resolvePreviewPath(
                serviceItem.getId(),
                form.getPreviewImage(),
                serviceItem.getPreviewImagePath()
        );

        serviceItem.setBannerImagePath(bannerImagePath);
        serviceItem.setPreviewImagePath(previewImagePath);

        return serviceItemRepository.save(serviceItem);
    }

    public void delete(Long id) {
        log.debug("Delete service with id={}", id);

        ServiceItem serviceItem = getById(id);
        fileStorageService.delete(serviceItem.getBannerImagePath());
        fileStorageService.delete(serviceItem.getPreviewImagePath());
        serviceItemRepository.delete(serviceItem);
    }

    @Transactional(readOnly = true)
    public Long countByFilters(AdminServiceItemFilterForm filter) {
        log.debug("Call method countByFilters for serviceItem");

        Specification<ServiceItem> specification = buildSpecification(filter);
        return serviceItemRepository.count(specification);
    }

    private void fillCommonFields(AdminServiceItemCreateOrUpdateForm form, ServiceItem serviceItem) {
        serviceItem.setName(form.getName().trim());
        serviceItem.setDescription(form.getDescription().trim());
        serviceItem.setIsVisible(form.getIsVisible());
    }

    private Specification<ServiceItem> buildSpecification(AdminServiceItemFilterForm filter) {
        return Specification.where(ServiceItemSpecifications.nameContains(filter.getName())
                .and(ServiceItemSpecifications.hasVisibleStatus(filter.getIsVisible())));
    }

    private String resolveBannerPath(Long serviceItemId, MultipartFile bannerImage, String currentPath) {
        if (bannerImage == null || bannerImage.isEmpty()) return normalizeBlank(currentPath);
        fileStorageService.delete(currentPath);
        return fileStorageService.store(bannerImage,
                "services/" + serviceItemId + "/banner");
    }

    private String resolvePreviewPath(Long serviceItemId, MultipartFile previewImage, String currentPath) {
        if (previewImage == null || previewImage.isEmpty()) return normalizeBlank(currentPath);
        fileStorageService.delete(currentPath);
        return fileStorageService.store(previewImage,
                "services/" + serviceItemId + "/preview");
    }

    private String normalizeBlank(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
