package com.tournabay.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tournabay.api.model.beatmap.Beatmap;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class BeatmapModification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean hidden;
    private Modification modification;
    private Integer position;

    @OrderBy("position ASC")
    @OneToMany(mappedBy = "beatmapModification", cascade = CascadeType.ALL)
    private List<Beatmap> beatmaps;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JsonIgnore
    private Mappool mappool;

    @PreUpdate
    @PrePersist
    public void preUpdate() {
        if (beatmaps != null) {
            for (int i = 0; i < beatmaps.size(); i++) {
                Beatmap beatmap = beatmaps.get(i);
                beatmap.setPosition(i);
                beatmap.setBeatmapModification(this);
            }
        }
    }

    @PreRemove
    public void preRemove() {
        mappool.getBeatmapModifications().remove(this);
    }
}
