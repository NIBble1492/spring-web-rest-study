package com.back.domain.member.member.controller;

import com.back.domain.member.member.dto.MemberDto;
import com.back.domain.member.member.entity.Member;
import com.back.domain.member.member.service.MemberService;
import com.back.domain.post.post.dto.PostDto;
import com.back.domain.post.post.entity.Post;
import com.back.global.globalExceptionHandler.UnauthenticatedException;
import com.back.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
@Tag(name = "ApiV1MemberController", description = "API 회원가입 컨트롤러")
public class ApiV1MemberController {
    private final MemberService memberService;

    public record MemberWriteReqBody (
            @NotBlank
            @Size(min = 4, max = 30)
            String username,
            @NotBlank
            @Size(min = 8, max = 30)
            String password,
            @NotBlank
            @Size(min = 2, max = 30)
            String name
    ) {
    }
    @PostMapping("/join")
    @Transactional
    @Operation(summary = "회원가입")
    public RsData<Void> join(
            @RequestBody @Valid MemberWriteReqBody reqBody
    ) {
        Member member = memberService.join(reqBody.username, reqBody.password, reqBody.name);

        return new RsData<>(
                "201-1",
                "%s님 환영합니다. 회원가입이 완료되었습니다.".formatted(reqBody.name)
        );
    }

    @GetMapping("/me")
    @Operation(summary = "회원 조회")
    public MemberDto getProfile(
            @RequestParam(required  = false) Integer actorId
    ) {
        // actorId 파라미터가 누락된 경우(null) 예외 처리
        if (actorId == null) {
            throw new UnauthenticatedException();
        }

        // 존재하지 않는 회원 번호(actorId)인 경우 예외 처리
        Member loginMember = memberService.findById(actorId).orElseThrow(
                UnauthenticatedException::new
        );

        return new MemberDto(loginMember);
    }

}