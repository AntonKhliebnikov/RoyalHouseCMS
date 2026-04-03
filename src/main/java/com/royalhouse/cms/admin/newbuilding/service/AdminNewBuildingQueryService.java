package com.royalhouse.cms.admin.newbuilding.service;

import com.royalhouse.cms.admin.newbuilding.dto.*;
import com.royalhouse.cms.core.common.embeddable.Address;
import com.royalhouse.cms.core.common.embeddable.GeoLocation;
import com.royalhouse.cms.core.newbuilding.entity.*;
import com.royalhouse.cms.core.newbuilding.exception.NewBuildingNotFoundException;
import com.royalhouse.cms.core.newbuilding.repository.*;
import com.royalhouse.cms.core.newbuilding.specification.NewBuildingSpecifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Log4j2
public class AdminNewBuildingQueryService {
    private final NewBuildingRepository newBuildingRepository;
    private final NewBuildingInfographicRepository newBuildingInfographicRepository;
    private final NewBuildingAboutSlideRepository newBuildingAboutSlideRepository;
    private final NewBuildingInfrastructureSlideRepository newBuildingInfrastructureSlideRepository;
    private final NewBuildingApartmentsSlideRepository newBuildingApartmentsSlideRepository;
    private final NewBuildingSpecificationBlockRepository newBuildingSpecificationBlockRepository;

    public Page<NewBuilding> findAll(AdminNewBuildingFilterForm filter, Pageable pageable) {
        log.debug("Find all new buildings by filter");

        Specification<NewBuilding> specification = buildSpecification(filter);
        return newBuildingRepository.findAll(specification, pageable);
    }

    public NewBuilding getById(Long id) {
        log.debug("Find new building with id={}", id);

        return newBuildingRepository.findById(id).orElseThrow(
                () -> new NewBuildingNotFoundException(id)
        );
    }

    public AdminNewBuildingBasicForm getBasicForm(NewBuilding newBuilding) {
        log.debug("Get form for the \"Basic\" tab by new building id={}", newBuilding.getId());

        AdminNewBuildingBasicForm form = new AdminNewBuildingBasicForm();
        form.setName(newBuilding.getName());
        form.setCurrentBannerImagePath(newBuilding.getBannerImagePath());
        form.setSortOrder(newBuilding.getSortOrder());
        form.setIsActive(newBuilding.getIsActive());

        List<AdminNewBuildingInfographicItemForm> items = newBuildingInfographicRepository
                .findAllByNewBuilding_IdAndSectionOrderBySortOrderAsc(
                        newBuilding.getId(),
                        NewBuildingInfographicSection.BASIC
                )
                .stream()
                .map(this::mapToInfographicItemForm)
                .collect(Collectors.toCollection(ArrayList::new));

        form.setBasicInfographics(items);
        return form;
    }

    public AdminNewBuildingAboutForm getAboutForm(NewBuilding newBuilding) {
        log.debug("Get form for the \"About the Project\" tab by id={}", newBuilding.getId());

        AdminNewBuildingAboutForm form = new AdminNewBuildingAboutForm();
        form.setAboutDescription(newBuilding.getAboutDescription());
        List<NewBuildingAboutSlide> slides =
                newBuildingAboutSlideRepository.findAllByNewBuilding_IdOrderBySlideNumberAsc(newBuilding.getId());

        for (NewBuildingAboutSlide slide : slides) {
            if (slide.getSlideNumber() == 1) {
                form.setCurrentSlide1ImagePath(slide.getImagePath());
            } else if (slide.getSlideNumber() == 2) {
                form.setCurrentSlide2ImagePath(slide.getImagePath());
            } else if (slide.getSlideNumber() == 3) {
                form.setCurrentSlide3ImagePath(slide.getImagePath());
            }
        }
        return form;
    }

    public AdminNewBuildingLocationForm getLocationForm(NewBuilding newBuilding) {
        log.debug("Get form for the \"Location\" tab by id={}", newBuilding.getId());

        AdminNewBuildingLocationForm form = new AdminNewBuildingLocationForm();

        Address address = newBuilding.getAddress();
        if (address != null) {
            form.setCity(address.getCity());
            form.setDistrict(address.getDistrict());
            form.setStreet(address.getStreet());
            form.setHouseNumber(address.getHouseNumber());
        }

        GeoLocation geoLocation = newBuilding.getGeoLocation();
        if (geoLocation != null) {
            form.setLatitude(geoLocation.getLatitude());
            form.setLongitude(geoLocation.getLongitude());
        }

        form.setLocationDescription(newBuilding.getLocationDescription());
        return form;
    }

    public AdminNewBuildingInfrastructureForm getInfrastructureForm(NewBuilding newBuilding) {
        log.debug("Get form for the \"Infrastructure\" tab by id={}", newBuilding.getId());

        AdminNewBuildingInfrastructureForm form = new AdminNewBuildingInfrastructureForm();
        form.setInfrastructureDescription(newBuilding.getInfrastructureDescription());

        List<NewBuildingInfrastructureSlide> slides =
                newBuildingInfrastructureSlideRepository.findAllByNewBuilding_IdOrderBySlideNumberAsc(newBuilding.getId());

        for (NewBuildingInfrastructureSlide slide : slides) {
            if (slide.getSlideNumber() == 1) {
                form.setCurrentSlide1ImagePath(slide.getImagePath());
            } else if (slide.getSlideNumber() == 2) {
                form.setCurrentSlide2ImagePath(slide.getImagePath());
            } else if (slide.getSlideNumber() == 3) {
                form.setCurrentSlide3ImagePath(slide.getImagePath());
            }
        }

        form.setInfrastructureInfographics(
                newBuildingInfographicRepository
                        .findAllByNewBuilding_IdAndSectionOrderBySortOrderAsc(newBuilding.getId(), NewBuildingInfographicSection.INFRASTRUCTURE)
                        .stream()
                        .map(this::mapToInfographicItemForm)
                        .collect(Collectors.toCollection(ArrayList::new))
        );

        return form;
    }

    public AdminNewBuildingApartmentsForm getApartmentsForm(NewBuilding newBuilding) {
        log.debug("Get form for the \"Apartments\" tab by id={}", newBuilding.getId());

        AdminNewBuildingApartmentsForm form = new AdminNewBuildingApartmentsForm();
        form.setApartmentsDescription(newBuilding.getApartmentsDescription());

        List<NewBuildingApartmentsSlide> slides =
                newBuildingApartmentsSlideRepository.findAllByNewBuilding_IdOrderBySlideNumberAsc(newBuilding.getId());

        for (NewBuildingApartmentsSlide slide : slides) {
            if (slide.getSlideNumber() == 1) {
                form.setCurrentSlide1ImagePath(slide.getImagePath());
            } else if (slide.getSlideNumber() == 2) {
                form.setCurrentSlide2ImagePath(slide.getImagePath());
            } else if (slide.getSlideNumber() == 3) {
                form.setCurrentSlide3ImagePath(slide.getImagePath());
            }
        }

        form.setApartmentsInfographics(
                newBuildingInfographicRepository
                        .findAllByNewBuilding_IdAndSectionOrderBySortOrderAsc(newBuilding.getId(), NewBuildingInfographicSection.APARTMENTS)
                        .stream()
                        .map(this::mapToInfographicItemForm)
                        .collect(Collectors.toCollection(ArrayList::new))
        );

        return form;
    }

    public AdminNewBuildingPanoramaForm getPanoramaForm(NewBuilding newBuilding) {
        log.debug("Get form for the \"Panorama\" tab by id={}", newBuilding.getId());

        AdminNewBuildingPanoramaForm form = new AdminNewBuildingPanoramaForm();
        form.setCurrentPanoramaImagePath(newBuilding.getPanoramaImagePath());
        return form;
    }

    public AdminNewBuildingSpecificationForm getSpecificationForm(NewBuilding newBuilding) {
        log.debug("Get form for the \"Specification\" tab by id={}", newBuilding.getId());

        AdminNewBuildingSpecificationForm form = new AdminNewBuildingSpecificationForm();

        List<AdminNewBuildingSpecificationBlockForm> blocks =
                newBuildingSpecificationBlockRepository
                        .findAllByNewBuilding_IdOrderBySortOrderAsc(newBuilding.getId())
                        .stream()
                        .map(this::mapToSpecificationBlockForm)
                        .collect(Collectors.toCollection(ArrayList::new));

        form.setBlocks(blocks);
        return form;
    }

    public Long countByFilters(AdminNewBuildingFilterForm filter) {
        log.debug("Call method countByFilters for new building");

        Specification<NewBuilding> specification = buildSpecification(filter);
        return newBuildingRepository.count(specification);
    }

    private Specification<NewBuilding> buildSpecification(AdminNewBuildingFilterForm filter) {
        return Specification.where(NewBuildingSpecifications.nameContains(filter.getName()))
                .and(NewBuildingSpecifications.addressContains(filter.getAddress()))
                .and(NewBuildingSpecifications.hasActiveStatus(filter.getIsActive()));
    }

    private AdminNewBuildingInfographicItemForm mapToInfographicItemForm(NewBuildingInfographic infographic) {
        AdminNewBuildingInfographicItemForm form = new AdminNewBuildingInfographicItemForm();
        form.setSortOrder(infographic.getSortOrder());
        form.setDescription(infographic.getDescription());
        form.setCurrentImagePath(infographic.getImagePath());
        return form;
    }

    private AdminNewBuildingSpecificationBlockForm mapToSpecificationBlockForm(NewBuildingSpecificationBlock block) {
        AdminNewBuildingSpecificationBlockForm form = new AdminNewBuildingSpecificationBlockForm();
        form.setSortOrder(block.getSortOrder());
        form.setContent(block.getContent());
        return form;
    }
}
