package com.tournabay.api.service;

import com.tournabay.api.model.Role;
import com.tournabay.api.model.RoleName;
import com.tournabay.api.repository.RoleRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RoleServiceTest {

    @Test
    void getBasicRoles() {
        // given

        // when

        // then
    }

    @Test
    void should_return_user_role() {
        // given
        RoleService roleService = mock(RoleService.class);
        given(roleService.getByName(RoleName.ROLE_USER)).willReturn(new Role(1L, RoleName.ROLE_USER));

        // when
        Role role = roleService.getByName(RoleName.ROLE_USER);

        // then
        assertEquals(RoleName.ROLE_USER, role.getName());
        assertEquals(1L, role.getId());
    }

}
