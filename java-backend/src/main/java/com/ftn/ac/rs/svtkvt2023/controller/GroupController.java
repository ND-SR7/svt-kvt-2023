package com.ftn.ac.rs.svtkvt2023.controller;

import com.ftn.ac.rs.svtkvt2023.model.dto.GroupDTO;
import com.ftn.ac.rs.svtkvt2023.model.entity.Group;
import com.ftn.ac.rs.svtkvt2023.model.entity.User;
import com.ftn.ac.rs.svtkvt2023.security.TokenUtils;
import com.ftn.ac.rs.svtkvt2023.service.GroupService;
import com.ftn.ac.rs.svtkvt2023.service.PostService;
import com.ftn.ac.rs.svtkvt2023.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/groups")
public class GroupController {

    GroupService groupService;

    UserService userService;

    PostService postService;

    AuthenticationManager authenticationManager;

    TokenUtils tokenUtils;

    @Autowired
    public GroupController(GroupService groupService, UserService userService, PostService postService,
                           AuthenticationManager authenticationManager, TokenUtils tokenUtils) {
        this.groupService = groupService;
        this.userService = userService;
        this.postService = postService;
        this.authenticationManager = authenticationManager;
        this.tokenUtils = tokenUtils;
    }

    @GetMapping()
    public ResponseEntity<List<GroupDTO>> getAll(@RequestHeader("authorization") String token) {
        String cleanToken = token.substring(7); //izbacivanje 'Bearer' iz tokena
        String username = tokenUtils.getUsernameFromToken(cleanToken); //izvlacenje username-a iz tokena
        User user = userService.findByUsername(username); //provera da li postoji u bazi

        if (user == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        List<Group> groups = groupService.findAll();
        List<GroupDTO> groupDTOS = new ArrayList<>();

        for (Group group: groups) {
            groupDTOS.add(new GroupDTO(group));
        }

        return new ResponseEntity<>(groupDTOS, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupDTO> getOne(@PathVariable String id,
                                           @RequestHeader("authorization") String token) {
        String cleanToken = token.substring(7); //izbacivanje 'Bearer' iz tokena
        String username = tokenUtils.getUsernameFromToken(cleanToken); //izvlacenje username-a iz tokena
        User user = userService.findByUsername(username); //provera da li postoji u bazi

        if (user == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Group group = groupService.findById(Long.parseLong(id));

        if (group == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        GroupDTO groupDTO = new GroupDTO(group);

        return new ResponseEntity<>(groupDTO, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<GroupDTO> createGroup(@RequestBody @Validated GroupDTO newGroup,
                              @RequestHeader("authorization") String token) {
        String cleanToken = token.substring(7); //izbacivanje 'Bearer' iz tokena
        String username = tokenUtils.getUsernameFromToken(cleanToken); //izvlacenje username-a iz tokena
        User user = userService.findByUsername(username); //provera da li postoji u bazi

        if (user == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Group createdGroup = groupService.createGroup(newGroup);

        if (createdGroup == null)
            return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);

        GroupDTO groupDTO = new GroupDTO(createdGroup);

        return new ResponseEntity<>(groupDTO, HttpStatus.CREATED);
    }

    @PatchMapping("/edit/{id}")
    public ResponseEntity<GroupDTO> editGroup(@PathVariable String id,
                                              @RequestBody @Validated GroupDTO editedGroup,
                                              @RequestHeader("authorization") String token) {
        String cleanToken = token.substring(7); //izbacivanje 'Bearer' iz tokena
        String username = tokenUtils.getUsernameFromToken(cleanToken); //izvlacenje username-a iz tokena
        User user = userService.findByUsername(username); //provera da li postoji u bazi

        if (user == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Group oldGroup = groupService.findById(Long.parseLong(id));

        if (oldGroup == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        oldGroup.setName(editedGroup.getName());
        oldGroup.setDescription(editedGroup.getDescription());
        oldGroup.setCreationDate(LocalDateTime.parse(editedGroup.getCreationDate()));
        oldGroup.setSuspended(editedGroup.isSuspended());
        oldGroup.setSuspendedReason(editedGroup.getSuspendedReason());

        oldGroup = groupService.updateGroup(oldGroup);

        GroupDTO updatedGroup = new GroupDTO(oldGroup);

        return new ResponseEntity<>(updatedGroup, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteGroup(@PathVariable String id,
                                      @RequestHeader("authorization") String token) {
        String cleanToken = token.substring(7); //izbacivanje 'Bearer' iz tokena
        String username = tokenUtils.getUsernameFromToken(cleanToken); //izvlacenje username-a iz tokena
        User user = userService.findByUsername(username); //provera da li postoji u bazi

        if (user == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        groupService.deleteGroupMembers(Long.parseLong(id));
        groupService.deleteGroupAdmins(Long.parseLong(id));
        Integer deleted = groupService.deleteGroup(Long.parseLong(id));

        if (deleted != 0)
            return new ResponseEntity(deleted, HttpStatus.NO_CONTENT);
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/delete/{groupId}/admin/{id}")
    public ResponseEntity deleteGroupAdmin(@PathVariable String groupId, @PathVariable String id,
                                      @RequestHeader("authorization") String token) {
        String cleanToken = token.substring(7); //izbacivanje 'Bearer' iz tokena
        String username = tokenUtils.getUsernameFromToken(cleanToken); //izvlacenje username-a iz tokena
        User user = userService.findByUsername(username); //provera da li postoji u bazi

        if (user == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Integer deleted = groupService.deleteGroupAdmin(Long.parseLong(groupId), Long.parseLong(id));

        if (deleted != 0)
            return new ResponseEntity(deleted, HttpStatus.NO_CONTENT);
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
}
