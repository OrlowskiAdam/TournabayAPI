package com.tournabay.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

@Entity
@SuperBuilder
@Data
@NoArgsConstructor
@DynamicUpdate
@Table(name = "\"match\"")
public abstract class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Start date cannot be null!")
    private LocalDate startDate;

    @NotNull(message = "Start time cannot be null!")
    private LocalTime startTime;

    @NotNull(message = "Date cannot be null!")
    private LocalDateTime ldt;

    @NotNull
    private Boolean isCompleted;

    @NotNull
    private Boolean isLive;

    @NotNull
    @Min(value = 1, message = "Referees limit must be greater than 0")
    private Integer refereesLimit;

    @NotNull
    @Min(value = 1, message = "Commentators limit must be greater than 0")
    private Integer commentatorsLimit;

    @NotNull
    @Min(value = 1, message = "Streamers limit must be greater than 0")
    private Integer streamersLimit;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    private Set<StaffMember> referees;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    private Set<StaffMember> commentators;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    private Set<StaffMember> streamers;

    @OneToOne(cascade = CascadeType.ALL)
    private MatchResult matchResult;

    @NotNull(message = "Stage cannot be null")
    private Stage stage;

    @PrePersist
    @PreUpdate
    private void preSave() {
        this.ldt = LocalDateTime.of(this.startDate, this.startTime);
    }
}
