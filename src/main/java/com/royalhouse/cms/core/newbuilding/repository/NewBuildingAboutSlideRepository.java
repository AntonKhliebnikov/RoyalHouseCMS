package com.royalhouse.cms.core.newbuilding.repository;

import com.royalhouse.cms.core.newbuilding.entity.NewBuildingAboutSlide;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NewBuildingAboutSlideRepository extends JpaRepository<NewBuildingAboutSlide, Long> {
    List<NewBuildingAboutSlide> findAllByNewBuilding_IdOrderBySlideNumberAsc(Long newBuildingId);

    Optional<NewBuildingAboutSlide> findByNewBuilding_idAndSlideNumber(Long newBuildingId, Short slideNumber);
}
