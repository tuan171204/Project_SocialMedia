package com.example.project_socialmedia.controllers.Request.Like;

import jakarta.annotation.Nullable;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LikeRequest {
    @Nullable
    private Long postId;
    @Nullable
    private Long commentId;

    private LocalDateTime createdAt;
}
