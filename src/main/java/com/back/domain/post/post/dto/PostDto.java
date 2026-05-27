package com.back.domain.post.post.dto;

import com.back.domain.member.member.entity.Member;
import com.back.domain.post.post.entity.Post;

import java.time.LocalDateTime;

public record PostDto(
        int id,
        LocalDateTime createDate,
        LocalDateTime modifyDate,
        String title,
        String content,
        String author // 작성자 이름 (요청 시의 actorId에 해당하는 회원의 이름)
) {

    public PostDto(Post post) {
        this(
                post.getId(),
                post.getCreateDate(),
                post.getModifyDate(),
                post.getTitle(),
                post.getContent(),
                post.getMember().getName()
        );
    }
}