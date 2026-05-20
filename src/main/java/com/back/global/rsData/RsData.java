package com.back.global.rsData;

import com.back.domain.post.postComment.dto.PostCommentDto;

public record RsData<T>(String resultCode, String msg, T data) {
}