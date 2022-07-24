package com.tournabay.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Entity
@SuperBuilder
@Data
@NoArgsConstructor
@DynamicUpdate
@Table(name = "\"match\"")
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate startDate;

    private LocalTime startTime;

    private Boolean isCompleted;

    private Boolean isLive;

    @OneToMany
    private Set<StaffMember> referees;

    @OneToMany
    private Set<StaffMember> commentators;

    @OneToMany
    private Set<StaffMember> streamers;
}
