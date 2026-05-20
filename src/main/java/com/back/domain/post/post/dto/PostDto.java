package com.back.domain.post.post.dto;

import com.back.domain.post.post.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostDto {
    private final int id;
    private final LocalDateTime createdDate;
    private final LocalDateTime modifiedDate;
    private final String subject;
    private final String body;

    public PostDto(Post post) {
        this.id = post.getId();
        this.createdDate = post.getCreateDate();
        this.modifiedDate = post.getModifyDate();
        this.subject = post.getTitle();
        this.body = post.getContent();
    }
}