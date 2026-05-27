package com.back.domain.member.member.service;

import com.back.domain.member.member.entity.Member;
import com.back.domain.member.member.repository.MemberRepository;
import com.back.domain.post.post.entity.Post;
import com.back.global.globalExceptionHandler.MemberDuplicateUsernameException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public Optional<Member> findLatest() {
        return memberRepository.findFirstByOrderByIdDesc();
    }

    public Optional<Member> findById(int id) {
        return memberRepository.findById(id);
    }

    public Member join(String username, String password, String name) {
        memberRepository.findByUsername(username)
                .ifPresent(_ -> {
                    throw new MemberDuplicateUsernameException(username);
                });

        Member member = new Member(username, password, name);

        return memberRepository.save(member);
    }
}