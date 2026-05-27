package com.back.domain.post.postComment.entity;

import com.back.domain.member.member.entity.Member;
import com.back.domain.post.post.entity.Post;
import com.back.global.jpa.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class PostComment extends BaseEntity {
    @ManyToOne
    @JsonIgnore
    private Post post;
    private String content;
    @ManyToOne
    @JsonIgnore
    private Member member;

    public PostComment(Post post, String content, Member member) {
        this.post = post;
        this.content = content;
        this.member = member;
    }

    public void modify(String content) {
        this.content = content;
    }
}