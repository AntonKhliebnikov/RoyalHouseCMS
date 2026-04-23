package com.royalhouse.cms.core.contact.repository;

import com.royalhouse.cms.core.contact.entity.ContactSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactSettingsRepository extends JpaRepository<ContactSettings, Long> {
}
