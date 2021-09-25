package com.tournabay.api.jackson.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserProperty {
    @JsonProperty("user_id")
    private Long osuId;

    @JsonProperty("username")
    private String username;

    @JsonProperty("pp_rank")
    private int rank;

    @JsonProperty("pp_country_rank")
    private int countryRank;

    @JsonProperty("country")
    private String country;

    @JsonProperty("pp_raw")
    private double performancePoints;
}
