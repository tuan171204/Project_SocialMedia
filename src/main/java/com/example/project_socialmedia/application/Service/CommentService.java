package com.example.project_socialmedia.application.Service;

import com.example.project_socialmedia.application.DTO.CommentDTO;
import com.example.project_socialmedia.application.Exception.ResourceNotFound;
import com.example.project_socialmedia.application.Service_Interface.ICommentService;
import com.example.project_socialmedia.controllers.Request.Comment.CommentCreateRequest;
import com.example.project_socialmedia.controllers.Request.Comment.CommentUpdateRequest;
import com.example.project_socialmedia.domain.Model.Comment;
import com.example.project_socialmedia.domain.Model.MediaAssociation;
import com.example.project_socialmedia.domain.Model.Post;
import com.example.project_socialmedia.domain.Model.User;
import com.example.project_socialmedia.domain.Repository.CommentRepository;
import com.example.project_socialmedia.domain.Repository.MediaAssociationRepository;
import com.example.project_socialmedia.domain.Repository.PostRepository;
import com.example.project_socialmedia.domain.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService implements ICommentService {
    private final ModelMapper modelMapper;

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    private final MediaService mediaService;
    private final String uploadDir = "gui/src/assets/uploads/posts/comments";
    private final MediaAssociationRepository mediaAssociationRepository;

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
    public List<Comment> getAllCommentsByUserId(Long userId) {
        User existingUser = userRepository.findUserByUserId(userId);
        return existingUser.getComments();
    }

    /**
     * Get All User Comment From Post
     *
     * @param postId Long
     */
    @Override
    public List<Comment> getAllCommentsByPostId(Long postId) {
        Post getPost = postRepository.findPostByPostId(postId);
        return getPost.getComments();
    }

    /**
     * Add Comment
     *
     * @param request Object {CommentCreateRequest}
     */
    @Override
    public Comment createComment(Long userId, Long postId, CommentCreateRequest request) {
        try {
            User existingUser = userRepository.findUserByUserId(userId);
            Post existingPost = postRepository.findPostByPostId(postId);

            Comment newComment = new Comment(
                    existingUser,
                    existingPost,
                    request.getContent(),
                    LocalDateTime.now(),    // Created At
                    LocalDateTime.now()     // Updated At
            );

            // Set CommentID first
            commentRepository.save(newComment);

            // Handle media
            List<MultipartFile> mediaFiles = request.getMediaFileRequest();
            if(mediaFiles != null) {
                mediaFiles.stream()
                        .filter(mediaFile -> !mediaFile.isEmpty())
                        .forEach(mediaFile -> mediaService.saveFile(
                                mediaFile,
                                uploadDir + newComment.getCommentId() + "/",
                                newComment.getCommentId(),
                                "Comment"
                        ));
            }
            return newComment;

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Delete Comment By ID
     *
     * @param commentId Long
     */
    @Override
    public void deleteCommentById(Long commentId) {
        try {
            Comment getComment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new ResourceNotFound("deleteCommentById: commentId not found"));

            // Remove related media
            List<MediaAssociation> mediaAssociationList = mediaAssociationRepository.findByTargetIdAndTargetType(commentId, "Comment");
            mediaAssociationList.forEach(mediaAssociation -> {
                mediaService.removeFile(commentId, "Comment", mediaAssociation.getMedia().getFileType());
            });

            commentRepository.delete(getComment);
        } catch (ResourceNotFound e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Update Comment
     *
     * @param request Object {CommentUpdateRequest}
     */
    @Override
    public Comment updateComment(Long userId, Long commentId, CommentUpdateRequest request) {
        Comment getComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFound("updateComment: commentId not found"));

        // TODO: handle media change if requested


        // Change
        getComment.setContent(request.getContent());
        getComment.setUpdatedAt(LocalDateTime.now());



        // Send to Database
        commentRepository.save(getComment);
        return getComment;
    }

    public CommentDTO convertToDTO(Comment comment) {
        CommentDTO mappedDTO = modelMapper.map(comment, CommentDTO.class);

        // Set PostId
        mappedDTO.setPostId(comment.getPost().getPostId());
        mappedDTO.setUserId(comment.getUser().getUserId());
        mappedDTO.setFirstName(comment.getUser().getFirstName());
        mappedDTO.setLastName(comment.getUser().getLastName());

        // TODO: Set Like


        return mappedDTO;
    }

    public List<CommentDTO> convertToDTOList(List<Comment> commentList) {
        return commentList.stream().map(this::convertToDTO).toList();
    }
}
