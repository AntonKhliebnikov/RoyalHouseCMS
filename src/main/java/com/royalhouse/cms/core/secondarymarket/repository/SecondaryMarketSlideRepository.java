package com.royalhouse.cms.core.secondarymarket.repository;

import com.royalhouse.cms.core.secondarymarket.entity.SecondaryMarketSlide;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SecondaryMarketSlideRepository extends JpaRepository<SecondaryMarketSlide, Long> {
    List<SecondaryMarketSlide> findAllByOrderBySortOrderAscIdAsc();

    List<SecondaryMarketSlide> findByIsActiveTrueOrderBySortOrderAscIdAsc();
}
