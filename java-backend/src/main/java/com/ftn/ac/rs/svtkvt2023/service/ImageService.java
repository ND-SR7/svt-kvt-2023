package com.ftn.ac.rs.svtkvt2023.service;

import com.ftn.ac.rs.svtkvt2023.model.dto.ImageDTO;
import com.ftn.ac.rs.svtkvt2023.model.entity.Image;

import java.util.List;

public interface ImageService {

    Image findById(Long id);

    List<Image> findImagesForPost(Long id);

    Image createImage(ImageDTO imageDTO);

    Integer deleteImage(Long id);
}
