package com.ftn.ac.rs.svtkvt2023.service;

import com.ftn.ac.rs.svtkvt2023.model.dto.GroupDTO;
import com.ftn.ac.rs.svtkvt2023.model.entity.Group;
import com.ftn.ac.rs.svtkvt2023.model.entity.Post;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public interface GroupService {

    Group findById(Long id);

    Group findByName(String name);

    List<Group> findByCreationDate(LocalDateTime creationDate);

    List<Group> findAll();

    List<Long> findPostsByGroupId(Long id);

    List<Group> findGroupsForUser(Long userId);

    Group checkIfPostInGroup(Long postId);

    Group createGroup(GroupDTO groupDTO, MultipartFile file);

    Group updateGroup(Group group);

    Integer deleteGroup(Long id);

    Boolean addGroupAdmin(Long groupId, Long adminId);

    Boolean addGroupMember(Long groupId, Long memberId);

    Integer deleteGroupAdmin(Long groupId, Long adminId);

    Integer deleteGroupMembers(Long id);

    Integer deleteGroupAdmins(Long id);

    Boolean checkUser(Long groupId, Long userId);
}
