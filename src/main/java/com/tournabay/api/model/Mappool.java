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

    @OneToMany(mappedBy = "mappool", cascade = CascadeType.ALL)
    private List<Beatmap> beatmaps = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Stage stage;

    private String name;

    @ManyToOne
    private Tournament tournament;
}
