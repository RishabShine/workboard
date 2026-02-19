package com.rishab.workboard.api.mapper;

import com.rishab.workboard.api.domain.Role;
import com.rishab.workboard.api.dto.response.common.RoleDto;
import com.rishab.workboard.api.mapper.impl.RoleMapperImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class RoleMapperTest {

    @Autowired
    private RoleMapperImpl roleMapper;

    @Test
    void toDto_mapsRoleFields() {
        Role role = new Role();
        role.setId(5L);
        role.setName("OWNER");

        RoleDto dto = roleMapper.toDto(role);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(5L);
        assertThat(dto.getName()).isEqualTo("OWNER");
    }
}
