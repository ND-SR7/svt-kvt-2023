package com.ftn.ac.rs.svtkvt2023.service;

import com.ftn.ac.rs.svtkvt2023.model.dto.GroupRequestDTO;
import com.ftn.ac.rs.svtkvt2023.model.entity.GroupRequest;

import java.util.List;

public interface GroupRequestService {

    GroupRequest findById(Long id);

    List<GroupRequest> findAllForGroup(Long groupId);

    GroupRequest createGroupRequest(GroupRequestDTO groupRequestDTO);

    GroupRequest updateGroupRequest(GroupRequest groupRequest);

    Integer deleteGroupRequest(Long id);
}
