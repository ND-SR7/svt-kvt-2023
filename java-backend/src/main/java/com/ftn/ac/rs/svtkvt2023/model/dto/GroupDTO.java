package com.ftn.ac.rs.svtkvt2023.model.dto;

import com.ftn.ac.rs.svtkvt2023.model.entity.Group;
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
public class GroupDTO {

    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    private LocalDateTime creationDate;

    @NotNull
    private boolean isSuspended;

    private String suspendedReason;

    public GroupDTO(Group createdGroup) {
        this.id = createdGroup.getId();
        this.name = createdGroup.getName();
        this.description = createdGroup.getDescription();
        this.creationDate = createdGroup.getCreationDate();
        this.isSuspended = createdGroup.isSuspended();
        this.suspendedReason = createdGroup.getSuspendedReason();
    }
}
