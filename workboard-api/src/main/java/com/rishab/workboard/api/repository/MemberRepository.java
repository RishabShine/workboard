package com.rishab.workboard.api.repository;

import com.rishab.workboard.api.domain.Member;
import com.rishab.workboard.api.domain.Project;
import com.rishab.workboard.api.domain.Role;
import com.rishab.workboard.api.domain.id.MemberId;
import com.rishab.workboard.api.repository.custom.MemberRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, MemberId>, MemberRepositoryCustom {
    // include isUserInProject
}