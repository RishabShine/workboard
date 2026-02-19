package com.rishab.workboard.api.mapper;

import com.rishab.workboard.api.domain.User;
import com.rishab.workboard.api.dto.response.common.UserSummaryDto;
import com.rishab.workboard.api.mapper.impl.UserMapperImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class UserMapperTest {

    @Autowired
    private UserMapperImpl userMapper;

    @Test
    void toDto_mapsUserSummaryFields() {
        User u = new User();
        u.setId(10L);
        u.setUsername("alice");
        u.setEmail("alice@example.com");
        u.setProfileImageKey("profile-image/10.png");

        UserSummaryDto dto = userMapper.toDto(u);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getUsername()).isEqualTo("alice");
        assertThat(dto.getProfileImageKey()).isEqualTo("profile-image/10.png");
    }

}
