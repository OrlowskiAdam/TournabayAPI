package com.tournabay.api.jackson.beatmap;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BeatmapProperty {
    @JsonProperty("beatmapset_id")
    private Long mapsetId;
    @JsonProperty("beatmap_id")
    private Long beatmapId;
    @JsonProperty("title")
    private String title;
    @JsonProperty("artist")
    private String artist;
    @JsonProperty("version")
    private String difficultyName;
    @JsonProperty("creator_id")
    private Long creatorId;
    @JsonProperty("creator")
    private String creator;
    @JsonProperty("total_length")
    private int length;

    @JsonProperty("diff_size")
    private Float cs;
    @JsonProperty("diff_approach")
    private Float ar;
    @JsonProperty("diff_drain")
    private Float hp;
    @JsonProperty("diff_overall")
    private Float od;
    @JsonProperty("difficultyrating")
    private Float starRating;
}
