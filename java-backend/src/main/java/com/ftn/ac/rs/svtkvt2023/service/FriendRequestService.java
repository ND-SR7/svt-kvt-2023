package com.ftn.ac.rs.svtkvt2023.service;

import com.ftn.ac.rs.svtkvt2023.model.dto.FriendRequestDTO;
import com.ftn.ac.rs.svtkvt2023.model.entity.FriendRequest;

import java.util.List;

public interface FriendRequestService {

    FriendRequest findById(Long id);

    List<FriendRequest> findAllFromUser(Long userId);

    List<FriendRequest> findAllToUser(Long userId);

    FriendRequest createFriendRequest(FriendRequestDTO friendRequestDTO);

    FriendRequest updateFriendRequest(FriendRequest friendRequest);

    Integer deleteFriendRequest(Long id);
}
