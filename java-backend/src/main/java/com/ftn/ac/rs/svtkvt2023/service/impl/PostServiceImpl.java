package com.ftn.ac.rs.svtkvt2023.service.impl;

import com.ftn.ac.rs.svtkvt2023.model.dto.PostDTO;
import com.ftn.ac.rs.svtkvt2023.model.entity.Post;
import com.ftn.ac.rs.svtkvt2023.model.entity.User;
import com.ftn.ac.rs.svtkvt2023.repository.PostRepository;
import com.ftn.ac.rs.svtkvt2023.service.GroupService;
import com.ftn.ac.rs.svtkvt2023.service.PostService;
import com.ftn.ac.rs.svtkvt2023.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PostServiceImpl implements PostService {

    private PostRepository postRepository;

    @Autowired
    public void setPostRepository(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    private GroupService groupService;

    @Autowired
    public void setGroupService(GroupService groupService) {
        this.groupService = groupService;
    }

    @Override
    public Post findById(Long id) {
        Optional<Post> post = postRepository.findById(id);
        if (!post.isEmpty())
            return post.get();
        return null;
    }

    @Override
    public List<Post> findByCreationDate(LocalDateTime creationDate) {
        Optional<List<Post>> posts = postRepository.findAllByCreationDate(creationDate);
        if (!posts.isEmpty())
            return posts.get();
        return null;
    }

    @Override
    public List<Post> findByPostedBy(User user) {
        Optional<List<Post>> posts = postRepository.findAllByPostedBy(user);
        if (!posts.isEmpty())
            return posts.get();
        return null;
    }

    @Override
    public List<Post> findAll() {
        return this.postRepository.findAll();
    }

    @Override
    public Post createPost(PostDTO postDTO) {
        Optional<Post> post = postRepository.findById(postDTO.getId());

        if (post.isPresent())
            return null;

        Post newPost = new Post();
        newPost.setContent(postDTO.getContent());
        newPost.setCreationDate(LocalDateTime.parse(postDTO.getCreationDate()));
        newPost.setPostedBy(userService.findById(postDTO.getPostedByUserId()));
        newPost.setDeleted(false);

        if (postDTO.getBelongsToGroupId() != null) {
            boolean userInGroup = groupService.checkUser(postDTO.getBelongsToGroupId(), postDTO.getPostedByUserId());

            if (!userInGroup)
                return null;
        }

        newPost = postRepository.save(newPost);

        if (postDTO.getBelongsToGroupId() != null)
            postRepository.saveGroupPost(postDTO.getBelongsToGroupId(), newPost.getId());

        return newPost;
    }

    @Override
    public Post updatePost(Post post) {
        return postRepository.save(post);
    }

    @Override
    public Integer deletePost(Long id) {
        return postRepository.deletePostById(id);
    }

    @Override
    public Integer deletePostFromGroup(Long id) {
        return postRepository.deletePostFromGroup(id);
    }
}
