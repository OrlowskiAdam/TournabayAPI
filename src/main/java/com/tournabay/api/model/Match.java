package com.tournabay.api.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalTime startTime;

    @Transient
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

    @ManyToMany
    private Set<StaffMember> referees;

    @ManyToMany
    private Set<StaffMember> commentators;

    @ManyToMany
    private Set<StaffMember> streamers;

    @OneToOne
    private MatchResult matchResult;

    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id"
    )
    @ManyToOne
    private Tournament tournament;

    @PostLoad
    private void postLoad() {
        this.ldt = LocalDateTime.of(this.startDate, this.startTime);
    }
}
