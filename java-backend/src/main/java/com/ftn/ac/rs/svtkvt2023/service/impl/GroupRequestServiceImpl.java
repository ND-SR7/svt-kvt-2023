package com.ftn.ac.rs.svtkvt2023.service.impl;

import com.ftn.ac.rs.svtkvt2023.model.dto.GroupRequestDTO;
import com.ftn.ac.rs.svtkvt2023.model.entity.Group;
import com.ftn.ac.rs.svtkvt2023.model.entity.GroupRequest;
import com.ftn.ac.rs.svtkvt2023.model.entity.User;
import com.ftn.ac.rs.svtkvt2023.repository.GroupRequestRepository;
import com.ftn.ac.rs.svtkvt2023.service.GroupRequestService;
import com.ftn.ac.rs.svtkvt2023.service.GroupService;
import com.ftn.ac.rs.svtkvt2023.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class GroupRequestServiceImpl implements GroupRequestService {

    private GroupRequestRepository groupRequestRepository;

    @Autowired
    public void setGroupRequestRepository(GroupRequestRepository groupRequestRepository) {
        this.groupRequestRepository = groupRequestRepository;
    }

    private GroupService groupService;

    @Autowired
    public void setGroupService(GroupService groupService) {
        this.groupService = groupService;
    }

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    private static final Logger logger = LogManager.getLogger(GroupRequestServiceImpl.class);

    @Override
    public GroupRequest findById(Long id) {
        Optional<GroupRequest> groupRequest = groupRequestRepository.findById(id);
        if (groupRequest.isPresent())
            return groupRequest.get();
        logger.error("Repository search for group request with id: {} returned null", id);
        return null;
    }

    @Override
    public List<GroupRequest> findAllForGroup(Long groupId) {
        Optional<List<GroupRequest>> groupRequests = groupRequestRepository.findAllByForGroup(groupId);
        if (groupRequests.isPresent())
            return groupRequests.get();
        logger.error("Repository search for group requests for group with id: {} returned null", groupId);
        return null;
    }

    @Override
    public GroupRequest createGroupRequest(GroupRequestDTO groupRequestDTO) {
        Optional<GroupRequest> groupRequest = groupRequestRepository.findById(groupRequestDTO.getId());

        if (groupRequest.isPresent()) {
            logger.error("Group request with id: {} already exists in repository", groupRequestDTO.getId());
            return null;
        }

        GroupRequest newGroupRequest = new GroupRequest();
        newGroupRequest.setCreatedAt(LocalDateTime.parse(groupRequestDTO.getCreatedAt()));

        if (groupRequestDTO.getAt() != null)
            newGroupRequest.setAt(LocalDateTime.parse(groupRequestDTO.getAt()));

        User user = userService.findById(groupRequestDTO.getCreatedByUserId());
        if (user == null)
            return null;

        Group group = groupService.findById(groupRequestDTO.getForGroupId());
        if (group == null)
            return null;

        newGroupRequest.setCreatedBy(user);
        newGroupRequest.setForGroup(group);

        newGroupRequest = groupRequestRepository.save(newGroupRequest);
        return newGroupRequest;
    }

    @Override
    public GroupRequest updateGroupRequest(GroupRequest groupRequest) {
        return groupRequestRepository.save(groupRequest);
    }

    @Override
    public Integer deleteGroupRequest(Long id) {
        return groupRequestRepository.deleteGroupRequestById(id);
    }
}
