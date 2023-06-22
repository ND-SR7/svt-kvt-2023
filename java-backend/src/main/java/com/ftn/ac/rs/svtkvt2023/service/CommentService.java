package com.ftn.ac.rs.svtkvt2023.service;

import com.ftn.ac.rs.svtkvt2023.model.dto.CommentDTO;
import com.ftn.ac.rs.svtkvt2023.model.entity.Comment;

import java.util.List;

public interface CommentService {

    Comment findById(Long id);

    List<Comment> findCommentsForPost(Long postId);

    Comment createComment(CommentDTO commentDTO);

    Comment updateComment(Comment comment);

    Integer deleteComment(Long id);

    Integer deleteCommentReply(Long commentId);

    Integer deletePostComments(Long postId);

    Integer deleteUserComments(Long userId);
}
