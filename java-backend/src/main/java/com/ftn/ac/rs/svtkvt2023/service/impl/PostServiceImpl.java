package com.ftn.ac.rs.svtkvt2023.service.impl;

import com.ftn.ac.rs.svtkvt2023.exception.LoadingException;
import com.ftn.ac.rs.svtkvt2023.indexmodel.GroupIndex;
import com.ftn.ac.rs.svtkvt2023.indexmodel.PostIndex;
import com.ftn.ac.rs.svtkvt2023.indexrepository.GroupIndexRepository;
import com.ftn.ac.rs.svtkvt2023.indexrepository.PostIndexRepository;
import com.ftn.ac.rs.svtkvt2023.model.dto.PostDTO;
import com.ftn.ac.rs.svtkvt2023.model.entity.Post;
import com.ftn.ac.rs.svtkvt2023.model.entity.User;
import com.ftn.ac.rs.svtkvt2023.repository.PostRepository;
import com.ftn.ac.rs.svtkvt2023.service.FileService;
import com.ftn.ac.rs.svtkvt2023.service.GroupService;
import com.ftn.ac.rs.svtkvt2023.service.PostService;
import com.ftn.ac.rs.svtkvt2023.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PostServiceImpl implements PostService {

    private PostRepository postRepository;
    private UserService userService;
    private GroupService groupService;
    private PostIndexRepository postIndexRepository;
    private GroupIndexRepository groupIndexRepository;
    private FileService fileService;

    @Autowired
    public void setPostRepository(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setGroupService(GroupService groupService) {
        this.groupService = groupService;
    }

    @Autowired
    public void setPostIndexRepository(PostIndexRepository postIndexRepository) {
        this.postIndexRepository = postIndexRepository;
    }

    @Autowired
    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }

    @Autowired
    public void setGroupIndexRepository(GroupIndexRepository groupIndexRepository) {
        this.groupIndexRepository = groupIndexRepository;
    }

    private static final Logger logger = LogManager.getLogger(PostServiceImpl.class);

    @Override
    public Post findById(Long id) {
        Optional<Post> post = postRepository.findById(id);
        if (post.isPresent())
            return post.get();
        logger.error("Repository search for post with id: {} returned null", id);
        return null;
    }

    @Override
    public List<Post> findByCreationDate(LocalDateTime creationDate) {
        Optional<List<Post>> posts = postRepository.findAllByCreationDate(creationDate);
        if (posts.isPresent())
            return posts.get();
        logger.error("Repository search for post created on date: {} returned null", creationDate.toString());
        return null;
    }

    @Override
    public List<Post> findByPostedBy(User user) {
        Optional<List<Post>> posts = postRepository.findAllByPostedBy(user);
        if (posts.isPresent())
            return posts.get();
        logger.error("Repository search for post created by user with id: {} returned null", user.getId());
        return null;
    }

    @Override
    public List<Post> findAll() {
        return this.postRepository.findAll();
    }

    @Override
    public List<Post> findHomepagePosts(Long userId) {
        Optional<List<Post>> posts = postRepository.findHomepagePosts(userId);
        if (posts.isPresent())
            return posts.get();
        logger.error("Repository search for homepage posts for user with id: {} returned null", userId);
        return null;
    }

    @Override
    public List<Post> findHomepagePostsSortedAsc(Long userId) {
        Optional<List<Post>> posts = postRepository.findHomepagePostsSortedAsc(userId);
        if (posts.isPresent())
            return posts.get();
        logger.error("Repository search for asc sorted homepage posts for user with id: {} returned null", userId);
        return null;
    }

    @Override
    public List<Post> findHomepagePostsSortedDesc(Long userId) {
        Optional<List<Post>> posts = postRepository.findHomepagePostsSortedDesc(userId);
        if (posts.isPresent())
            return posts.get();
        logger.error("Repository search for desc sorted homepage posts for user with id: {} returned null", userId);
        return null;
    }

    @Override
    public Post createPost(PostDTO postDTO, MultipartFile file) {
        Optional<Post> post = postRepository.findById(postDTO.getId());

        if (post.isPresent()) {
            logger.error("Post with id: {} already exists in repository", postDTO.getId());
            return null;
        }

        Post newPost = new Post();
        newPost.setTitle(postDTO.getTitle());
        newPost.setContent(postDTO.getContent());
        newPost.setCreationDate(LocalDateTime.parse(postDTO.getCreationDate()));
        newPost.setPostedBy(userService.findById(postDTO.getPostedByUserId()));
        newPost.setDeleted(false);

        String filename = fileService.store(file, UUID.randomUUID().toString());
        newPost.setContentFilename(filename);

        if (postDTO.getBelongsToGroupId() != null) {
            boolean userInGroup = groupService.checkUser(postDTO.getBelongsToGroupId(), postDTO.getPostedByUserId());

            if (!userInGroup) {
                logger.error("User with id: {} tried posting in group with id: {} while not being a member",
                        postDTO.getPostedByUserId(), postDTO.getBelongsToGroupId());
                return null;
            }
        }

        newPost = postRepository.save(newPost);

        if (postDTO.getBelongsToGroupId() != null) {
            GroupIndex groupIndex = groupIndexRepository.findByDatabaseId(
                    postDTO.getBelongsToGroupId()).orElse(null);

            if (groupIndex == null) {
                logger.warn("Index not found for group containing post");
            } else {
                groupIndex.setNumberOfPosts(groupIndex.getNumberOfPosts() + 1);
                groupIndexRepository.save(groupIndex);
            }
            postRepository.saveGroupPost(postDTO.getBelongsToGroupId(), newPost.getId());
        }

        PostIndex index = new PostIndex();
        index.setTitle(postDTO.getTitle());
        index.setFullContent(postDTO.getContent());
        index.setFileContent(extractDocumentContent(file));
        index.setNumberOfLikes(0L);
        index.setNumberOfComments(0L);
        index.setCommentContent("");
        index.setDatabaseId(newPost.getId());
        postIndexRepository.save(index);

        return newPost;
    }

    @Override
    public Post updatePost(Post post) {
        PostIndex index = postIndexRepository.findByTitle(post.getTitle()).orElse(null);
        if (index == null) {
            logger.warn("No index found for post: {}", post.getTitle());
        } else {
            index.setFullContent(post.getContent());
            postIndexRepository.save(index);
        }
        return postRepository.save(post);
    }

    @Override
    public Integer deletePost(Long id) {
        postIndexRepository.deleteByDatabaseId(id);
        return postRepository.deletePostById(id);
    }

    @Override
    public Integer deletePostFromGroup(Long id) {
        Long groupId = postRepository.findGroupIdForPost(id).orElse(null);
        if (groupId != null) {
            groupIndexRepository
                    .findByDatabaseId(groupId)
                    .ifPresent(groupIndex -> {
                        groupIndex.setNumberOfPosts(groupIndex.getNumberOfPosts() - 1);
                        groupIndexRepository.save(groupIndex);
                    });

            postIndexRepository.deleteByDatabaseId(id);
        }
        return postRepository.deletePostFromGroup(id);
    }

    private String extractDocumentContent(MultipartFile multipartPdfFile) {
        String documentContent;
        try (InputStream pdfFile = multipartPdfFile.getInputStream()) {
            PDDocument pdDocument = PDDocument.load(pdfFile);
            PDFTextStripper textStripper = new PDFTextStripper();
            documentContent = textStripper.getText(pdDocument);
            pdDocument.close();
        } catch (IOException e) {
            throw new LoadingException("Error while trying to load PDF file content for post");
        }

        return documentContent;
    }
}
