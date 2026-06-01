package com.royalhouse.cms.core.aboutcompany.repository;

import com.royalhouse.cms.core.aboutcompany.entity.AboutCompanySettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AboutCompanySettingsRepository extends JpaRepository<AboutCompanySettings, Long> {
}
