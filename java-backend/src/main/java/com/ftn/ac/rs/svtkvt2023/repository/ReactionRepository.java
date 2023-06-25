package com.ftn.ac.rs.svtkvt2023.repository;

import com.ftn.ac.rs.svtkvt2023.model.entity.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Long> {

    @Query(nativeQuery = true,
            value = "select * from reactions where on_post_id = :postId and deleted = false")
    Optional<List<Reaction>> findAllByOnPostId(@Param("postId") Long postId);

    @Query(nativeQuery = true,
            value = "select * from reactions where on_comment_id = :commentId and deleted = false")
    Optional<List<Reaction>> findAllByOnCommentId(@Param("commentId") Long commentId);

    @Transactional
    Integer deleteReactionById(Long id);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,
            value = "update reactions set deleted = true where on_post_id = :postId")
    Integer deleteReactionsByOnPostId(@Param("postId") Long postId);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,
            value = "update reactions set deleted = true where on_comment_id = :commentId")
    Integer deleteReactionsByOnCommentId(@Param("commentId") Long commentId);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,
            value = "update reactions set deleted = true where made_by_user_id = :userId")
    Integer deleteReactionsMadeByUserId(@Param("userId") Long userId);
}
