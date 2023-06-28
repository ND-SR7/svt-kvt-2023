package com.ftn.ac.rs.svtkvt2023.model.dto;

import com.ftn.ac.rs.svtkvt2023.model.entity.FriendRequest;
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
public class FriendRequestDTO {

    private Long id = -1L;

    @NotNull
    private boolean approved;

    @NotBlank
    private String createdAt;

    private String at; //vreme odgovora

    @NotNull
    private Long fromUserId;

    @NotNull
    private Long toUserId;

    public FriendRequestDTO(FriendRequest friendRequest) {
        this.id = friendRequest.getId();
        this.approved = friendRequest.getApproved();
        this.createdAt = friendRequest.getCreatedAt().toString();
        this.at = friendRequest.getAt().toString();
        this.fromUserId = friendRequest.getFrom().getId();
        this.toUserId = friendRequest.getTo().getId();
    }
}
