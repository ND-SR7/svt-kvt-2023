package com.ftn.ac.rs.svtkvt2023.service.impl;

import com.ftn.ac.rs.svtkvt2023.exception.LoadingException;
import com.ftn.ac.rs.svtkvt2023.indexmodel.GroupIndex;
import com.ftn.ac.rs.svtkvt2023.indexrepository.GroupIndexRepository;
import com.ftn.ac.rs.svtkvt2023.model.dto.GroupDTO;
import com.ftn.ac.rs.svtkvt2023.model.entity.Group;
import com.ftn.ac.rs.svtkvt2023.repository.GroupRepository;
import com.ftn.ac.rs.svtkvt2023.service.FileService;
import com.ftn.ac.rs.svtkvt2023.service.GroupService;
import com.ftn.ac.rs.svtkvt2023.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GroupServiceImpl implements GroupService {

    private GroupRepository groupRepository;
    private UserService userService;
    private GroupIndexRepository groupIndexRepository;
    private FileService fileService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setGroupRepository(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Autowired
    public void setGroupIndexRepository(GroupIndexRepository groupIndexRepository) {
        this.groupIndexRepository = groupIndexRepository;
    }

    @Autowired
    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }

    private static final Logger logger = LogManager.getLogger(GroupServiceImpl.class);

    @Override
    public Group findById(Long id) {
        Optional<Group> group = groupRepository.findById(id);
        if (group.isPresent())
            return group.get();
        logger.error("Repository search for group with id: {} returned null", id);
        return null;
    }

    @Override
    public Group findByName(String name) {
        Optional<Group> group = groupRepository.findByName(name);
        if (group.isPresent())
            return group.get();
        logger.error("Repository search for group with name: {} returned null", name);
        return null;
    }

    @Override
    public List<Group> findByCreationDate(LocalDateTime creationDate) {
        Optional<List<Group>> groups = groupRepository.findAllByCreationDate(creationDate);
        if (groups.isPresent())
            return groups.get();
        logger.error("Repository search for group created on date: {} returned null", creationDate.toString());
        return null;
    }

    @Override
    public List<Group> findAll() {
        return this.groupRepository.findAll();
    }

    @Override
    public List<Long> findPostsByGroupId(Long id) {
        Optional<List<Long>> postsIds = groupRepository.findPostsByGroupId(id);
        if (postsIds.isPresent())
            return postsIds.get();
        logger.error("Repository search for posts for group with id: {} returned null", id);
        return null;
    }

    @Override
    public List<Group> findGroupsForUser(Long userId) {
        Optional<List<Group>> groups = groupRepository.findGroupsByMemberId(userId);
        if (groups.isPresent())
            return groups.get();
        logger.error("Repository search for groups for user with id: {} returned null", userId);
        return null;
    }

    @Override
    public Group checkIfPostInGroup(Long postId) {
        Optional<Group> group = groupRepository.checkIfPostInGroup(postId);
        if (group.isPresent())
            return group.get();
        logger.error("Repository search in groups for post with id: {} returned null", postId);
        return null;
    }

    @Override
    public Group createGroup(GroupDTO groupDTO, MultipartFile file) {
        Optional<Group> group = groupRepository.findByName(groupDTO.getName());

        if (group.isPresent()) {
            logger.error("Group with id: {} already exists in repository", groupDTO.getId());
            return null;
        }

        Group newGroup = new Group();
        newGroup.setName(groupDTO.getName());
        newGroup.setDescription(groupDTO.getDescription());
        newGroup.setCreationDate(LocalDateTime.parse(groupDTO.getCreationDate()));
        newGroup.setSuspended(groupDTO.isSuspended());
        newGroup.setSuspendedReason(groupDTO.getSuspendedReason());
        newGroup.setRules(groupDTO.getRules());
        newGroup.setDeleted(false);

        String filename = fileService.store(file, UUID.randomUUID().toString());
        newGroup.setRulesFilename(filename);

        newGroup = groupRepository.save(newGroup);

        GroupIndex index = new GroupIndex();
        index.setName(groupDTO.getName());
        index.setDescription(groupDTO.getDescription());
        index.setFileContent(extractDocumentContent(file));
        index.setNumberOfPosts(0L);
        index.setRules(groupDTO.getRules());
        index.setAverageLikes(0.0);
        index.setDatabaseId(newGroup.getId());
        groupIndexRepository.save(index);

        return newGroup;
    }

    @Override
    public Group updateGroup(Group group) {
        GroupIndex index = groupIndexRepository.findByName(group.getName()).orElse(null);
        if (index == null) {
            logger.warn("No index found for group: {}", group.getName());
        } else {
            index.setName(group.getName());
            index.setDescription(group.getDescription());
            index.setRules(group.getRules());
            groupIndexRepository.save(index);
        }
        return groupRepository.save(group);
    }

    @Override
    public Integer deleteGroup(Long id) {
        groupIndexRepository.deleteByDatabaseId(id);
        return groupRepository.deleteGroupById(id);
    }

    @Override
    public Boolean addGroupAdmin(Long groupId, Long adminId) {
        return groupRepository.addGroupAdmin(groupId, adminId) > 0;
    }

    @Override
    public Boolean addGroupMember(Long groupId, Long memberId) {
        return groupRepository.addGroupMember(groupId, memberId) > 0;
    }

    @Override
    public Integer deleteGroupAdmin(Long groupId, Long adminId) {
        return groupRepository.deleteGroupAdmin(groupId, adminId);
    }

    @Override
    public Integer deleteGroupMembers(Long id) {
        return groupRepository.deleteGroupMembers(id);
    }

    @Override
    public Integer deleteGroupAdmins(Long id) {
        return groupRepository.deleteGroupAdmins(id);
    }

    @Override
    public Boolean checkUser(Long groupId, Long userId) {
        return (groupRepository.findUserInGroup(groupId, userId) > 0 || userService.checkUserIsAdmin(userId));
    }

    private String extractDocumentContent(MultipartFile multipartPdfFile) {
        String documentContent;
        try (InputStream pdfFile = multipartPdfFile.getInputStream()) {
            PDDocument pdDocument = PDDocument.load(pdfFile);
            PDFTextStripper textStripper = new PDFTextStripper();
            documentContent = textStripper.getText(pdDocument);
            pdDocument.close();
        } catch (IOException e) {
            throw new LoadingException("Error while trying to load PDF file content for group");
        }

        return documentContent;
    }
}
