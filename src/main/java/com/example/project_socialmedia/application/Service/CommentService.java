package com.example.project_socialmedia.application.Service;

import com.example.project_socialmedia.application.Service_Interface.ICommentService;
import com.example.project_socialmedia.domain.Modal.Comment;
import com.example.project_socialmedia.domain.Modal.Post;
import com.example.project_socialmedia.domain.Modal.User;
import com.example.project_socialmedia.domain.Repository.CommentRepository;
import com.example.project_socialmedia.domain.Repository.PostRepository;
import com.example.project_socialmedia.domain.Repository.UserRepository;
import com.example.project_socialmedia.application.Request.Comment.CommentCreateRequest;
import com.example.project_socialmedia.application.Request.Comment.CommentUpdateRequest;
import com.example.project_socialmedia.controllers.Exception.ResourceNotFound;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService implements ICommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    /**
     * Get Comment By ID
     *
     * @param id Long
     * @return Object {Comment}
     */
    @Override
    public Comment getCommentById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("getCommentById: commentId not found"));
    }

    /**
     * Get All Comment
     *
     * @return List{Object}: {Comment}
     */
    @Override
    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    /**
     * Get All User Comments By User ID
     *
     * @param userId Long
     * @return List{Object}: {Comment}
     */
    @Override
    public List<Comment> getAllUserCommentsByUserId(Long userId) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFound("getAllUserCommentsByUserId: userId not found"));

        // Testing
        System.out.println("Retrieved User: " + existingUser.getUserId());
        System.out.println("User Comments: " + existingUser.getComments()
                .stream()
                .map(comment ->
                        "comment: " + comment.getContent() + " created date: " + comment.getCreatedAt())
                .toList()
        );

        return existingUser.getComments();
    }

    /**
     * Get All User Comment From Post
     * TODO: getAllUserCommentsByPostId [Need Testing]
     *
     * @param postId    Long
     */
    @Override
    public List<Comment> getAllUserCommentsByPostId(Long postId) {
        Post getPost = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFound("getAllUserCommentsByPostId: postId not found"));

        return getPost.getComments();
    }

    /**
     * Add Comment
     * TODO: addComment [Need Testing]
     *
     * @param request Object {CommentCreateRequest}
     */
    @Override
    public Comment addComment(CommentCreateRequest request, Long userId, Long postId) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFound("addComment: userId not found"));

        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFound("addComment: postId not found"));

        return new Comment(
                existingUser,
                existingPost,
                request.getContent(),
                LocalDateTime.now(),    // Created At
                LocalDateTime.now()     // Updated At
        );
    }

    /**
     * Delete Comment By ID
     * TODO: deleteCommentById [Need Testing]
     *
     * @param commentId Long
     */
    @Override
    public void deleteCommentById(Long commentId) {
        Comment getComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFound("deleteCommentById: commentId not found"));

        commentRepository.delete(getComment);
    }

    /**
     * Update Comment
     * TODO: updateComment [Need Testing]
     *
     * @param request Object {CommentUpdateRequest}
     */
    @Override
    public void updateComment(CommentUpdateRequest request, Long commentId) {
        Comment getComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFound("updateComment: commentId not found"));

            // Change
        getComment.setContent(request.getContent());
        getComment.setUpdatedAt(LocalDateTime.now());

        // Send to Database
        commentRepository.save(getComment);
    }
}
