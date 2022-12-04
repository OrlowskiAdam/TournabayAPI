package com.tournabay.api.dto;

import lombok.Getter;

@Getter
public class DiscordAccessTokenDto {
    private String access_token;
    private String token_type;
    private String expires_in;
    private String refresh_token;
    private String scope;
}
