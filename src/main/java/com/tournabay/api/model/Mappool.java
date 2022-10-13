package com.tournabay.api.model;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
@Setter
public class Mappool {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OrderBy("position ASC")
    @OneToMany(mappedBy = "mappool", cascade = CascadeType.ALL)
    private List<BeatmapModification> beatmapModifications = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Stage stage;

    private String name;

    private Boolean isReleased;

    @ManyToOne
    private Tournament tournament;

    @PreUpdate
    @PrePersist
    public void preUpdate() {
        if (beatmapModifications != null) {
            beatmapModifications.forEach(b -> b.setMappool(this));
        }
    }
}
