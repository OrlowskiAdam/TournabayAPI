package com.tournabay.api.osu.model;

import lombok.Getter;

@Getter
public class OsuBeatmapset {
    private String artist;
    private String artist_unicode;
    private OsuBeatmapCovers covers;
    private String creator;
    private Long id;
    private String status;
    private String title;
    private String title_unicode;
    private Long user_id;
    private Float bpm;
    private Long max_combo;

}
