package com.ftn.ac.rs.svtkvt2023.service;

import com.ftn.ac.rs.svtkvt2023.model.dto.PostDTO;
import com.ftn.ac.rs.svtkvt2023.model.entity.Image;
import com.ftn.ac.rs.svtkvt2023.model.entity.Post;
import com.ftn.ac.rs.svtkvt2023.model.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public interface PostService {

    Post findById(Long id);

    List<Post> findByCreationDate(LocalDateTime creationDate);

    List<Post> findByPostedBy(User user);

    List<Post> findAll();

    List<Post> findHomepagePosts(Long userId);

    List<Post> findHomepagePostsSortedAsc(Long userId);
    List<Post> findHomepagePostsSortedDesc(Long userId);

    Post createPost(PostDTO postDTO, MultipartFile file);

    Post updatePost(Post post);

    Integer deletePost(Long id);

    Integer deletePostFromGroup(Long id);
}
