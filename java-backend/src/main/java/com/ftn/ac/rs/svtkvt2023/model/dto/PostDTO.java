package com.ftn.ac.rs.svtkvt2023.model.dto;

import com.ftn.ac.rs.svtkvt2023.model.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {

    private Long id = -1L; // kod kreiranja novog entiteta

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotNull
    private String creationDate;

    @NotNull
    private Long postedByUserId;

    private List<ImageDTO> images;

    private Long belongsToGroupId;

    @NotNull
    private MultipartFile file;

    public PostDTO(Post createdPost) {
        this.id = createdPost.getId();
        this.title = createdPost.getTitle();
        this.content = createdPost.getContent();
        this.creationDate = createdPost.getCreationDate().toString();
        this.postedByUserId = createdPost.getPostedBy().getId();
    }
}
