package com.royalhouse.cms.core.newbuilding.repository;

import com.royalhouse.cms.core.newbuilding.entity.NewBuildingInfrastructureSlide;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewBuildingInfrastructureSlideRepository extends JpaRepository<NewBuildingInfrastructureSlide, Long> {
    List<NewBuildingInfrastructureSlide> findAllByNewBuilding_IdOrderBySlideNumberAsc(Long newBuildingId);

    void deleteAllByNewBuilding_Id(Long newBuildingId);
}
