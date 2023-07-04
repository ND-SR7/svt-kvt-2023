package com.ftn.ac.rs.svtkvt2023.repository;

import com.ftn.ac.rs.svtkvt2023.model.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    @Query(nativeQuery = true,
            value = "select * from images where belongs_to_post_id = :id and deleted = false")
    Optional<List<Image>> findImagesForPost(@Param("id") Long id);

    @Query(nativeQuery = true,
            value = "select * from images where belongs_to_user_id = :userId and deleted = false")
    Optional<Image> findProfileImageForUser(@Param("userId") Long userId);

    @Transactional
    Integer deleteImageById(Long id);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,
            value = "update images set deleted = true where belongs_to_post_id = :postId")
    Integer deleteImagesByBelongsToPostId(@Param("postId") Long postId);
}
