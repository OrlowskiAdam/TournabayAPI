package com.tournabay.api.dto;

import lombok.Getter;

@Getter
public class DiscordUserInfoDto {
    private String id;
    private String username;
    private String avatar;
    private String discriminator;
    private Boolean bot;
    private Boolean mfa_enabled;
    private String locale;
    private Boolean verified;
    private String email;
    private Integer flags;
    private Integer premium_type;
}
