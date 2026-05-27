package com.back.domain.member.member.controller;

import com.back.domain.member.member.entity.Member;
import com.back.domain.member.member.service.MemberService;
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
            @Size(min = 4, max = 30)
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


}