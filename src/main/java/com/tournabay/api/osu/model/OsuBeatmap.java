package com.tournabay.api.osu.model;

import lombok.Getter;

@Getter
public class OsuBeatmap {
    private Long beatmapset_id;
    private Float difficulty_rating;
    private Long id;
    private String mode;
    private String status;
    private Long total_length;
    private Long user_id;
    private String version;
    private Float accuracy;
    private Float ar;
    private Float bpm;
    private Float cs;
    private Float drain;
    private String url;
    private Integer max_combo;
}
