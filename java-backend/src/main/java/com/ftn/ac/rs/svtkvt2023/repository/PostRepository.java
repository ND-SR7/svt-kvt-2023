package com.ftn.ac.rs.svtkvt2023.repository;

import com.ftn.ac.rs.svtkvt2023.model.entity.Post;
import com.ftn.ac.rs.svtkvt2023.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<List<Post>> findAllByCreationDate(LocalDateTime creationDate);

    Optional<List<Post>> findAllByPostedBy(User user);

    @Query(nativeQuery = true,
            value = "select * from posts where posted_by_user_id = :userId or (((posted_by_user_id = :userId\n" +
                    "\tor posted_by_user_id in (\n" +
                    "\t\tselect friend_id from user_friends where user_id = :userId)\n" +
                    "    or posted_by_user_id in (\n" +
                    "\t\tselect user_id from user_friends where friend_id = :userId))\n" +
                    "\tand id not in (\n" +
                    "\t\tselect post_id from group_posts where group_id not in (\n" +
                    "\t\t\tselect group_id from group_members where member_id = :userId)))\n" +
                    "    or id in (\n" +
                    "\t\tselect post_id from group_posts GP left join group_members GM on GP.group_id = GM.group_id where member_id = :userId))\n" +
                    "and deleted = false;")
    Optional<List<Post>> findHomepagePosts(@Param("userId") Long userId);

    @Query(nativeQuery = true,
            value = "select * from posts where (((posted_by_user_id = :userId\n" +
                    "\tor posted_by_user_id in (\n" +
                    "\t\tselect friend_id from user_friends where user_id = :userId)\n" +
                    "\tor posted_by_user_id in (\n" +
                    "\t\tselect user_id from user_friends where friend_id = :userId))\n" +
                    "\tand id not in (\n" +
                    "\t\tselect post_id from group_posts where group_id not in (\n" +
                    "\t\t\tselect group_id from group_members where member_id = :userId)))\n" +
                    "\tor id in (\n" +
                    "\t\tselect post_id from group_posts GP left join group_members GM on GP.group_id = GM.group_id where member_id = :userId))\n" +
                    "\tand deleted = false\n" +
                    "order by creation_date asc;")
    Optional<List<Post>> findHomepagePostsSortedAsc(@Param("userId") Long userId);

    @Query(nativeQuery = true,
            value = "select * from posts where (((posted_by_user_id = :userId\n" +
                    "\tor posted_by_user_id in (\n" +
                    "\t\tselect friend_id from user_friends where user_id = :userId)\n" +
                    "\tor posted_by_user_id in (\n" +
                    "\t\tselect user_id from user_friends where friend_id = :userId))\n" +
                    "\tand id not in (\n" +
                    "\t\tselect post_id from group_posts where group_id not in (\n" +
                    "\t\t\tselect group_id from group_members where member_id = :userId)))\n" +
                    "\tor id in (\n" +
                    "\t\tselect post_id from group_posts GP left join group_members GM on GP.group_id = GM.group_id where member_id = :userId))\n" +
                    "\tand deleted = false\n" +
                    "order by creation_date desc;")
    Optional<List<Post>> findHomepagePostsSortedDesc(@Param("userId") Long userId);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,
            value = "insert into group_posts (group_id, post_id) values (:groupId, :postId)")
    Integer saveGroupPost(@Param("groupId") Long groupId, @Param("postId") Long postId);

    @Transactional
    Integer deletePostById(Long id);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,
            value = "delete from group_posts where post_id = :id")
    Integer deletePostFromGroup(@Param("id") Long id);

    @Query(nativeQuery = true,
            value = "select group_id from group_posts where post_id = :postId")
    Optional<Long> findGroupIdForPost(@Param("postId") Long postId);
}
