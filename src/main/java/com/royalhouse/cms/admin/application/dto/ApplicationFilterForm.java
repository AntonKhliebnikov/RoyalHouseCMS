package com.royalhouse.cms.admin.application.dto;

import com.royalhouse.cms.core.application.entity.ApplicationStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationFilterForm {
    private String fullName;
    private String phone;
    private String email;
    private String comment;
    private ApplicationStatus status;
}
