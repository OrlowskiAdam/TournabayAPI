package com.tournabay.api.model.beatmap;

import com.tournabay.api.model.Modification;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Stats {
    @Id
    @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
    private Long id;

    private Float ar;
    private Float cs;
    private Float accuracy;
    private Float hp;

    private Float bpm;
    private Long maxCombo;
    private Integer length;
    private Float stars;

    private Modification modification;
}
