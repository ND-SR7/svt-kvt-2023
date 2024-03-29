package com.ftn.ac.rs.svtkvt2023.repository;

import com.ftn.ac.rs.svtkvt2023.model.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(nativeQuery = true,
            value = "select * from comments where belongs_to_post_id = :postId and deleted = false")
    Optional<List<Comment>> findCommentsForPost(@Param("postId") Long postId);

    @Transactional
    Integer deleteCommentById(Long id);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,
            value = "update comments set deleted = true where belongs_to_post_id = :postId")
    Integer deleteCommentsByBelongsToPostId(@Param("postId") Long postId);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,
            value = "update comments set deleted = true where replies_to_comment_id = :commentId")
    Integer deleteReplyToComment(@Param("commentId") Long commentId);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,
            value = "update comments set deleted = true where belongs_to_user_id = :userId")
    Integer deleteCommentsByBelongsToUserId(@Param("userId") Long userId);
}
