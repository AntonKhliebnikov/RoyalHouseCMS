package com.royalhouse.cms.core.newbuilding.repository;

import com.royalhouse.cms.core.newbuilding.entity.NewBuildingInfographic;
import com.royalhouse.cms.core.newbuilding.entity.NewBuildingInfographicSection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewBuildingInfographicRepository extends JpaRepository<NewBuildingInfographic, Long> {
    List<NewBuildingInfographic> findAllByNewBuilding_IdAndSectionOrderBySortOrderAsc(
            Long newBuildingId,
            NewBuildingInfographicSection section
    );

    void deleteAllByNewBuilding_IdAndSection(
            Long newBuildingId,
            NewBuildingInfographicSection section
    );
}