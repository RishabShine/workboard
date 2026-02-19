package com.rishab.workboard.api.mapper;

import com.rishab.workboard.api.domain.Project;
import com.rishab.workboard.api.domain.ProjectInvite;
import com.rishab.workboard.api.domain.User;
import com.rishab.workboard.api.domain.enums.InviteStatus;
import com.rishab.workboard.api.dto.response.project.ProjectInviteDto;
import com.rishab.workboard.api.mapper.impl.ProjectInviteMapperImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class ProjectInviteMapperTest {

    @Autowired
    private ProjectInviteMapperImpl inviteMapper;

    @Test
    void toDto_mapsAllFieldsCorrectly() {

        // Arrange

        User recipient = new User();
        recipient.setId(10L);
        recipient.setUsername("recipient");
        recipient.setEmail("recipient@example.com");
        recipient.setProfileImageKey("profile-image/10.png");

        User invitedBy = new User();
        invitedBy.setId(20L);
        invitedBy.setUsername("inviter");
        invitedBy.setEmail("inviter@example.com");
        invitedBy.setProfileImageKey("profile-image/20.png");

        Project project = new Project();
        project.setId(99L);
        project.setName("Workboard Project");

        ProjectInvite invite = new ProjectInvite();
        invite.setId(500L);
        invite.setProject(project);
        invite.setRecipient(recipient);
        invite.setInvitedBy(invitedBy);
        invite.setStatus(InviteStatus.PENDING);
        invite.setCreatedAt(OffsetDateTime.now());

        // Act

        ProjectInviteDto dto = inviteMapper.toDto(invite);

        // Assert

        assertThat(dto).isNotNull();

        assertThat(dto.getId()).isEqualTo(500L);
        assertThat(dto.getProjectId()).isEqualTo(99L);
        assertThat(dto.getProjectName()).isEqualTo("Workboard Project");

        assertThat(dto.getStatus()).isEqualTo(InviteStatus.PENDING);
        assertThat(dto.getCreatedAt()).isEqualTo(invite.getCreatedAt());

        assertThat(dto.getRecipient()).isNotNull();
        assertThat(dto.getRecipient().getId()).isEqualTo(10L);
        assertThat(dto.getRecipient().getUsername()).isEqualTo("recipient");

        assertThat(dto.getInvitedBy()).isNotNull();
        assertThat(dto.getInvitedBy().getId()).isEqualTo(20L);
        assertThat(dto.getInvitedBy().getUsername()).isEqualTo("inviter");
    }

}
