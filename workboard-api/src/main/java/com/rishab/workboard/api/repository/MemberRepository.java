package com.rishab.workboard.api.repository;

import com.rishab.workboard.api.domain.Member;
import com.rishab.workboard.api.domain.id.MemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, MemberId> {

    // include isUserInProject
}