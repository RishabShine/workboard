package com.rishab.workboard.api.domain;

import com.rishab.workboard.api.domain.id.MemberId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "members"
)
@NoArgsConstructor
public class Member {

    @EmbeddedId
    private MemberId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("projectId")
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "joined_on", nullable = false, insertable=false, updatable=false)
    private OffsetDateTime joinedOn;

    public Member(User user, Project project) {
        this.user = user;
        this.project = project;
        this.id = new MemberId(user.getId(), project.getId());
    }

}
