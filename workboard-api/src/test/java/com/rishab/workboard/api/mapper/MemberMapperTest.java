package com.rishab.workboard.api.mapper;

import com.rishab.workboard.api.domain.Member;
import com.rishab.workboard.api.domain.Role;
import com.rishab.workboard.api.domain.User;
import com.rishab.workboard.api.dto.response.project.MemberDto;
import com.rishab.workboard.api.mapper.impl.MemberMapperImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class MemberMapperTest {

    @Autowired
    private MemberMapperImpl memberMapper;

    @Test
    void toDto_mapsMemberFields() {
        User user = new User();
        user.setId(1L);
        user.setUsername("bob");
        user.setEmail("bob@example.com");
        user.setProfileImageKey("profile-image/1.png");

        Role role = new Role();
        role.setId(2L);
        role.setName("MEMBER");

        Member member = new Member();
        member.setRole(role);
        member.setJoinedOn(OffsetDateTime.now());
        member.setUser(user);

        MemberDto dto = memberMapper.toDto(member);

        assertThat(dto).isNotNull();
        assertThat(dto.getUser()).isNotNull();
        assertThat(dto.getUser().getId()).isEqualTo(1L);
        assertThat(dto.getUser().getUsername()).isEqualTo("bob");
        assertThat(dto.getEmail()).isEqualTo("bob@example.com");
        assertThat(dto.getRole().getId()).isEqualTo(2L);
        assertThat(dto.getRole().getName()).isEqualTo("MEMBER");
        assertThat(dto.getJoinedOn()).isEqualTo(member.getJoinedOn());
    }

}
