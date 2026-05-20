package com.back.global.rsData;

import com.back.domain.post.postComment.dto.PostCommentDto;

public record RsData(String resultCode, String msg, PostCommentDto data) {
}