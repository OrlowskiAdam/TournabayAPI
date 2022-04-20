package com.tournabay.api.payload;

import lombok.Getter;

@Getter
public class UpdateRegistrationSettingsRequest {
    private Boolean allowRegistration;
    private Boolean openRank;
    private Long maxRank;
    private Long minRank;
}
