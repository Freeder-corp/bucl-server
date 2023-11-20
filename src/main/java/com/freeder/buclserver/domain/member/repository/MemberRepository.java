package com.freeder.buclserver.domain.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.freeder.buclserver.domain.member.entity.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

	Optional<Member> findBySocialId(String socialId);
}
