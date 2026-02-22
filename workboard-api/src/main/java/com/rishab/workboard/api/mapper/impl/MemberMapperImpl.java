package com.rishab.workboard.api.mapper.impl;

import com.rishab.workboard.api.domain.Member;
import com.rishab.workboard.api.domain.User;
import com.rishab.workboard.api.dto.response.project.MemberDto;
import com.rishab.workboard.api.mapper.MemberMapper;
import org.springframework.stereotype.Component;

@Component
public class MemberMapperImpl implements MemberMapper {

    private final RoleMapperImpl roleMapper;
    private final MilestoneMapperImpl milestoneMapper;
    private final UserMapperImpl userMapper;

    public MemberMapperImpl(RoleMapperImpl roleMapper,
                                   UserMapperImpl userMapper,
                                   MilestoneMapperImpl milestoneMapper) {
        this.roleMapper = roleMapper;
        this.milestoneMapper = milestoneMapper;
        this.userMapper = userMapper;
    }

    @Override
    public MemberDto toDto(Member member) {

        MemberDto memberDto = new MemberDto();
        memberDto.setUser(userMapper.toDto(member.getUser()));
        memberDto.setEmail(member.getUser().getEmail());
        memberDto.setRole(roleMapper.toDto(member.getRole()));
        memberDto.setJoinedOn(member.getJoinedOn());

        return memberDto;
    }

}
