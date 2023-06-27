package com.ftn.ac.rs.svtkvt2023.service.impl;

import com.ftn.ac.rs.svtkvt2023.model.dto.ImageDTO;
import com.ftn.ac.rs.svtkvt2023.model.entity.Image;
import com.ftn.ac.rs.svtkvt2023.model.entity.Post;
import com.ftn.ac.rs.svtkvt2023.model.entity.User;
import com.ftn.ac.rs.svtkvt2023.repository.ImageRepository;
import com.ftn.ac.rs.svtkvt2023.service.ImageService;
import com.ftn.ac.rs.svtkvt2023.service.PostService;
import com.ftn.ac.rs.svtkvt2023.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ImageServiceImpl implements ImageService {

    private ImageRepository imageRepository;

    private PostService postService;

    @Autowired
    public void setPostService(PostService postService) {
        this.postService = postService;
    }

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setImageRepository(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @Override
    public Image findById(Long id) {
        Optional<Image> image = imageRepository.findById(id);
        if (!image.isEmpty())
            return image.get();
        return null;
    }

    @Override
    public List<Image> findImagesForPost(Long id) {
        Optional<List<Image>> images = imageRepository.findImagesForPost(id);
        if (!images.isEmpty())
            return images.get();
        return null;
    }

    @Override
    public Image findProfileImageForUser(Long userId) {
        Optional<Image> image = imageRepository.findProfileImageForUser(userId);
        if (!image.isEmpty())
            return image.get();
        return null;
    }

    @Override
    public Image createImage(ImageDTO imageDTO) {
        Optional<Image> image = imageRepository.findById(imageDTO.getId());

        if (image.isPresent())
            return null;

        Image newImage = new Image();
        newImage.setPath(imageDTO.getPath());

        if (imageDTO.getBelongsToPostId() != null) {
            Post post = postService.findById(imageDTO.getBelongsToPostId());
            newImage.setBelongsToPost(post);
        }

        if (imageDTO.getBelongsToUserId() != null) {
            User user = userService.findById(imageDTO.getBelongsToUserId());
            newImage.setBelongsToUser(user);
        }

        newImage.setDeleted(false);
        newImage = imageRepository.save(newImage);

        return newImage;
    }

    @Override
    public Integer deleteImage(Long id) {
        return imageRepository.deleteImageById(id);
    }

    @Override
    public Integer deletePostImages(Long postId) {
        return imageRepository.deleteImagesByBelongsToPostId(postId);
    }
}
