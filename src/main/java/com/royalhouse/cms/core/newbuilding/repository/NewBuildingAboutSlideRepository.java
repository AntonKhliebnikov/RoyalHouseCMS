package com.royalhouse.cms.core.newbuilding.repository;

import com.royalhouse.cms.core.newbuilding.entity.NewBuildingAboutSlide;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewBuildingAboutSlideRepository extends JpaRepository<NewBuildingAboutSlide, Long> {
    List<NewBuildingAboutSlide> findAllByNewBuilding_IdOrderBySlideNumberAsc(Long newBuildingId);

    void deleteAllByNewBuilding_Id(Long newBuildingId);
}
