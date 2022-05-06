package com.tournabay.api.service;

import com.tournabay.api.model.StaffMember;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class StaffMemberServiceTest {

    @Test
    void save() {
        // given
        StaffMemberService staffMemberService = mock(StaffMemberService.class);
        given(staffMemberService.save(any(StaffMember.class))).willReturn(new StaffMember());

        // when
        StaffMember staffMember = staffMemberService.save(new StaffMember());

        // then
        assertEquals(new StaffMember(), staffMember);
    }
}
