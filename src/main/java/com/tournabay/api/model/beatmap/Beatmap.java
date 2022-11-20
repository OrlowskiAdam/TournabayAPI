package com.tournabay.api.model.beatmap;

import com.tournabay.api.model.BeatmapModification;
import com.tournabay.api.model.Modification;
import lombok.*;
import lombok.experimental.SuperBuilder;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor
@SuperBuilder
@AllArgsConstructor
@Getter
@Setter
public class Beatmap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int position;

    private Long beatmapsetId;
    private Long beatmapId;

    private String artist;
    private String title;
    private String version;
    private String creator;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Stats> stats;

    private String normalCover;
    private String cardCover;
    private String listCover;
    private String slimCover;

    private Modification modification;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonIgnore
    private BeatmapModification beatmapModification;

    @PreRemove
    public void preRemove() {
        beatmapModification.getBeatmaps().remove(this);
    }

    @PostLoad
    public void postLoad() {
        if (beatmapModification != null) {
            modification = beatmapModification.getModification();
        }
    }
}
