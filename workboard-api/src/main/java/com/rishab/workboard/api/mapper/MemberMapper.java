package com.rishab.workboard.api.mapper;

import com.rishab.workboard.api.domain.Member;
import com.rishab.workboard.api.domain.User;
import com.rishab.workboard.api.dto.response.project.MemberDto;

public interface MemberMapper {

    MemberDto toDto(User user, Member member);

}
