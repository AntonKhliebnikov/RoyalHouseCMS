package com.royalhouse.cms.core.newbuilding.repository;

import com.royalhouse.cms.core.newbuilding.entity.NewBuildingApartmentSlide;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewBuildingApartmentSlideRepository extends JpaRepository<NewBuildingApartmentSlide, Long> {
    List<NewBuildingApartmentSlide> findAllByNewBuilding_IdOrderBySlideNumberAsc(Long newBuildingId);

    void deleteAllByNewBuilding_Id(Long newBuildingId);
}
