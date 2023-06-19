package com.ftn.ac.rs.svtkvt2023.model.dto;

import com.ftn.ac.rs.svtkvt2023.model.entity.Post;
import com.ftn.ac.rs.svtkvt2023.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {

    private Long id = -1L; // kod kreiranja novog entiteta

    @NotBlank
    private String content;

    @NotNull
    private String creationDate;

    @NotNull
    private Long postedByUserId;

    public PostDTO(Post createdPost) {
        this.id = createdPost.getId();
        this.content = createdPost.getContent();
        this.creationDate = createdPost.getCreationDate().toString();
        this.postedByUserId = createdPost.getPostedBy().getId();
    }
}
