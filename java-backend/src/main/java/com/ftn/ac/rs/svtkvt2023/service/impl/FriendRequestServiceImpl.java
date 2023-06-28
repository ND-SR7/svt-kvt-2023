package com.ftn.ac.rs.svtkvt2023.service.impl;

import com.ftn.ac.rs.svtkvt2023.model.dto.FriendRequestDTO;
import com.ftn.ac.rs.svtkvt2023.model.entity.FriendRequest;
import com.ftn.ac.rs.svtkvt2023.model.entity.User;
import com.ftn.ac.rs.svtkvt2023.repository.FriendRequestRepository;
import com.ftn.ac.rs.svtkvt2023.service.FriendRequestService;
import com.ftn.ac.rs.svtkvt2023.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FriendRequestServiceImpl implements FriendRequestService {

    private FriendRequestRepository friendRequestRepository;

    @Autowired
    public void setFriendRequestRepository(FriendRequestRepository friendRequestRepository) {
        this.friendRequestRepository = friendRequestRepository;
    }

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public FriendRequest findById(Long id) {
        Optional<FriendRequest> friendRequest = friendRequestRepository.findById(id);
        if (!friendRequest.isEmpty())
            return friendRequest.get();
        return null;
    }

    @Override
    public List<FriendRequest> findAllFromUser(Long userId) {
        Optional<List<FriendRequest>> friendRequests = friendRequestRepository.findAllByFromUser(userId);
        if (!friendRequests.isEmpty())
            return friendRequests.get();
        return null;
    }

    @Override
    public List<FriendRequest> findAllToUser(Long userId) {
        Optional<List<FriendRequest>> friendRequests = friendRequestRepository.findAllByToUser(userId);
        if (!friendRequests.isEmpty())
            return friendRequests.get();
        return null;
    }

    @Override
    public FriendRequest createFriendRequest(FriendRequestDTO friendRequestDTO) {
        Optional<FriendRequest> friendRequest = friendRequestRepository.findById(friendRequestDTO.getId());

        if (friendRequest.isPresent())
            return null;

        FriendRequest newFriendRequest = new FriendRequest();
        newFriendRequest.setCreatedAt(LocalDateTime.parse(friendRequestDTO.getCreatedAt()));
        if (friendRequestDTO.getAt() != null)
            newFriendRequest.setAt(LocalDateTime.parse(friendRequestDTO.getAt()));

        User fromUser = userService.findById(friendRequestDTO.getFromUserId());
        if (fromUser == null)
            return null;

        User toUser = userService.findById(friendRequestDTO.getToUserId());
        if (toUser == null)
            return null;

        newFriendRequest.setFrom(fromUser);
        newFriendRequest.setTo(toUser);

        newFriendRequest = friendRequestRepository.save(newFriendRequest);
        return newFriendRequest;
    }

    @Override
    public FriendRequest updateFriendRequest(FriendRequest friendRequest) {
        return friendRequestRepository.save(friendRequest);
    }

    @Override
    public Integer deleteFriendRequest(Long id) {
        return friendRequestRepository.deleteFriendRequestById(id);
    }
}
