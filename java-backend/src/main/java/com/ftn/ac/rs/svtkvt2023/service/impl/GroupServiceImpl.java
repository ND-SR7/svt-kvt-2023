package com.ftn.ac.rs.svtkvt2023.service.impl;

import com.ftn.ac.rs.svtkvt2023.model.dto.GroupDTO;
import com.ftn.ac.rs.svtkvt2023.model.entity.Group;
import com.ftn.ac.rs.svtkvt2023.repository.GroupRepository;
import com.ftn.ac.rs.svtkvt2023.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class GroupServiceImpl implements GroupService {

    private GroupRepository groupRepository;

    @Autowired
    public void setGroupRepository(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Override
    public Group findById(Long id) {
        Optional<Group> group = groupRepository.findById(id);
        if (!group.isEmpty())
            return group.get();
        return null;
    }

    @Override
    public Group findByName(String name) {
        Optional<Group> group = groupRepository.findByName(name);
        if (!group.isEmpty())
            return group.get();
        return null;
    }

    @Override
    public List<Group> findByCreationDate(LocalDateTime creationDate) {
        Optional<List<Group>> groups = groupRepository.findAllByCreationDate(creationDate);
        if (!groups.isEmpty())
            return groups.get();
        return null;
    }

    @Override
    public List<Group> findAll() {
        return this.groupRepository.findAll();
    }

    @Override
    public Group createGroup(GroupDTO groupDTO) {
        Optional<Group> group = groupRepository.findByName(groupDTO.getName());

        if (group.isPresent())
            return null;

        Group newGroup = new Group();
        newGroup.setName(groupDTO.getName());
        newGroup.setDescription(groupDTO.getDescription());
        newGroup.setCreationDate(groupDTO.getCreationDate());
        newGroup.setSuspended(groupDTO.isSuspended());
        newGroup.setSuspendedReason(groupDTO.getSuspendedReason());
        newGroup.setDeleted(false);
        newGroup = groupRepository.save(newGroup);

        return newGroup;
    }

    @Override
    public Group updateGroup(Group group) {
        return groupRepository.save(group);
    }

    @Override
    public Long deleteGroup(Long id) {
        return groupRepository.deleteGroupById(id);
    }
}