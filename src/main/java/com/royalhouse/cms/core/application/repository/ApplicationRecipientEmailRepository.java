package com.royalhouse.cms.core.application.repository;

import com.royalhouse.cms.core.application.entity.ApplicationRecipientEmail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRecipientEmailRepository extends JpaRepository<ApplicationRecipientEmail, Long> {
    List<ApplicationRecipientEmail> findAllByIsActiveTrue();
}
