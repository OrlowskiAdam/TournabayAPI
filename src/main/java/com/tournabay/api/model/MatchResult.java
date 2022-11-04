package com.tournabay.api.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
@Setter
public class MatchResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Min(value = 0, message = "Score must be greater than or equal to 0")
    private Long redScore;

    @NotNull
    @Min(value = 0, message = "Score must be greater than or equal to 0")
    private Long blueScore;

    private String lobbyLink;

}
