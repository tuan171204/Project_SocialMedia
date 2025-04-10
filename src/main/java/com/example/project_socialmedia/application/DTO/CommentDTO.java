package com.example.project_socialmedia.application.DTO;

import com.example.project_socialmedia.domain.Model.Like;
import com.example.project_socialmedia.domain.Model.Post;
import com.example.project_socialmedia.domain.Model.User;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentDTO {
    private Long commentId;
    private User user;
    private Post post;
    private List<Like> likeList;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
