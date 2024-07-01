package com.ftn.ac.rs.svtkvt2023.model.dto;

import com.ftn.ac.rs.svtkvt2023.model.entity.Group;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
    private String creationDate;

    @NotNull
    private boolean suspended;

    private String suspendedReason;

    @NotNull
    private String rules;

    @NotNull
    private String filename;

    public GroupDTO(Group createdGroup) {
        this.id = createdGroup.getId();
        this.name = createdGroup.getName();
        this.description = createdGroup.getDescription();
        this.creationDate = createdGroup.getCreationDate().toString();
        this.suspended = createdGroup.isSuspended();
        this.suspendedReason = createdGroup.getSuspendedReason();
        this.rules = createdGroup.getRules();
        this.filename = createdGroup.getRulesFilename();
    }
}
