package com.royalhouse.cms.admin.settings.secondarymarket.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SecondaryMarketSettingsForm {
    private List<SecondaryMarketSlideForm> slides = new ArrayList<>();
}
