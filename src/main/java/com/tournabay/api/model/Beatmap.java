package com.tournabay.api.model;

import lombok.*;
import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
@Setter
public class Beatmap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int position;

    private String artist;
    private String title;
    private String version;

    private Float ar;
    private Float cs;
    private Float od;
    private Float hp;

    private Float bpm;
    private Float length;
    private Float stars;

    private String normalCover;
    private String cardCover;
    private String listCover;
    private String slimCover;

    @Enumerated(EnumType.STRING)
    private Modification modification;

    @JsonIgnore
    @ManyToOne
    private Mappool mappool;
}
