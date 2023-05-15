package com.ftn.ac.rs.svtkvt2023.service;

import com.ftn.ac.rs.svtkvt2023.model.dto.GroupDTO;
import com.ftn.ac.rs.svtkvt2023.model.entity.Group;

import java.time.LocalDateTime;
import java.util.List;

public interface GroupService {

    Group findById(Long id);

    Group findByName(String name);

    List<Group> findByCreationDate(LocalDateTime creationDate);

    List<Group> findAll();

    Group createGroup(GroupDTO groupDTO);

    Group updateGroup(Group group);

    Long deleteGroup(Long id);
}
