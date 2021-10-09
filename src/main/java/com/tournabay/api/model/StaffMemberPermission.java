package com.tournabay.api.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class StaffMemberPermission extends Permission {

    @ManyToOne
    private StaffMember staffMember;
}
