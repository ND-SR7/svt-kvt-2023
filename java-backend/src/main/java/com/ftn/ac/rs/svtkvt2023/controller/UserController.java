package com.ftn.ac.rs.svtkvt2023.controller;

import com.ftn.ac.rs.svtkvt2023.model.dto.*;
import com.ftn.ac.rs.svtkvt2023.model.entity.FriendRequest;
import com.ftn.ac.rs.svtkvt2023.model.entity.Group;
import com.ftn.ac.rs.svtkvt2023.model.entity.Image;
import com.ftn.ac.rs.svtkvt2023.model.entity.User;
import com.ftn.ac.rs.svtkvt2023.security.TokenUtils;
import com.ftn.ac.rs.svtkvt2023.service.FriendRequestService;
import com.ftn.ac.rs.svtkvt2023.service.GroupService;
import com.ftn.ac.rs.svtkvt2023.service.ImageService;
import com.ftn.ac.rs.svtkvt2023.service.UserService;
import com.ftn.ac.rs.svtkvt2023.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/users")
public class UserController {

    UserService userService;

    UserDetailsService userDetailsService;

    ImageService imageService;

    GroupService groupService;

    FriendRequestService friendRequestService;

    AuthenticationManager authenticationManager;

    TokenUtils tokenUtils;

    @Autowired
    public UserController(UserServiceImpl userService, AuthenticationManager authenticationManager,
                          UserDetailsService userDetailsService, ImageService imageService, GroupService groupService,
                          FriendRequestService friendRequestService, TokenUtils tokenUtils){
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.imageService = imageService;
        this.groupService = groupService;
        this.friendRequestService = friendRequestService;
        this.tokenUtils = tokenUtils;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getOne(@PathVariable String id,
                                           @RequestHeader("authorization") String token) {
        String cleanToken = token.substring(7); //izbacivanje 'Bearer' iz tokena
        String username = tokenUtils.getUsernameFromToken(cleanToken); //izvlacenje username-a iz tokena
        User user = userService.findByUsername(username); //provera da li postoji u bazi

        if (user == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        User findUser = userService.findById(Long.parseLong(id));

        if (findUser == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        UserDTO userDTO = new UserDTO(findUser);

        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<ImageDTO> getProfileImage(@PathVariable String id,
                                           @RequestHeader("authorization") String token) {
        String cleanToken = token.substring(7); //izbacivanje 'Bearer' iz tokena
        String username = tokenUtils.getUsernameFromToken(cleanToken); //izvlacenje username-a iz tokena
        User user = userService.findByUsername(username); //provera da li postoji u bazi

        if (user == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Image image = imageService.findProfileImageForUser(Long.parseLong(id));

        if (image == null)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        ImageDTO imageDTO = new ImageDTO(image);

        return new ResponseEntity<>(imageDTO, HttpStatus.OK);
    }

    @GetMapping("/{id}/groups")
    public ResponseEntity<List<GroupDTO>> getUserGroups(@PathVariable String id,
                                                    @RequestHeader("authorization") String token) {
        String cleanToken = token.substring(7); //izbacivanje 'Bearer' iz tokena
        String username = tokenUtils.getUsernameFromToken(cleanToken); //izvlacenje username-a iz tokena
        User user = userService.findByUsername(username); //provera da li postoji u bazi

        if (user == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        List<Group> groups = groupService.findGroupsForUser(Long.parseLong(id));
        List<GroupDTO> groupDTOS = new ArrayList<>();

        for (Group group: groups) {
            groupDTOS.add(new GroupDTO(group));
        }

        return new ResponseEntity<>(groupDTOS, HttpStatus.OK);
    }

    @GetMapping("/friend-request")
    public ResponseEntity<List<FriendRequestDTO>> getFriendRequests(@RequestHeader("authorization") String token) {
        String cleanToken = token.substring(7); //izbacivanje 'Bearer' iz tokena
        String username = tokenUtils.getUsernameFromToken(cleanToken); //izvlacenje username-a iz tokena
        User user = userService.findByUsername(username); //provera da li postoji u bazi

        if (user == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        List<FriendRequest> friendRequestsFromUser = friendRequestService.findAllFromUser(user.getId());
        List<FriendRequest> friendRequestsToUser = friendRequestService.findAllToUser(user.getId());
        List<FriendRequestDTO> friendRequestDTOS = new ArrayList<>();

        for (FriendRequest friendRequest: friendRequestsFromUser)
            friendRequestDTOS.add(new FriendRequestDTO(friendRequest));

        for (FriendRequest friendRequest: friendRequestsToUser)
            friendRequestDTOS.add(new FriendRequestDTO(friendRequest));

        return new ResponseEntity<>(friendRequestDTOS, HttpStatus.OK);
    }

    @PostMapping("/{id}/friend-request")
    public ResponseEntity<Boolean> saveFriendRequest(@PathVariable String id,
                                                     @RequestBody @Validated FriendRequestDTO friendRequestDTO,
                                                     @RequestHeader("authorization") String token) {
        String cleanToken = token.substring(7); //izbacivanje 'Bearer' iz tokena
        String username = tokenUtils.getUsernameFromToken(cleanToken); //izvlacenje username-a iz tokena
        User user = userService.findByUsername(username); //provera da li postoji u bazi

        if (user == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        FriendRequest friendRequest = friendRequestService.createFriendRequest(friendRequestDTO);

        if (friendRequest == null)
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @GetMapping("/friends")
    public ResponseEntity<List<UserDTO>> getUserFriends(@RequestHeader("authorization") String token) {
        String cleanToken = token.substring(7); //izbacivanje 'Bearer' iz tokena
        String username = tokenUtils.getUsernameFromToken(cleanToken); //izvlacenje username-a iz tokena
        User user = userService.findByUsername(username); //provera da li postoji u bazi

        if (user == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        List<User> users = userService.findFriendsForUser(user.getId());
        List<UserDTO> userDTOS = new ArrayList<>();

        for (User temp: users) {
            userDTOS.add(new UserDTO(temp));
        }

        return new ResponseEntity<>(userDTOS, HttpStatus.OK);
    }

    @GetMapping("/user/{queryUsername}")
    public ResponseEntity<UserDTO> getOneByUsername(@PathVariable String queryUsername,
                                          @RequestHeader("authorization") String token) {
        String cleanToken = token.substring(7); //izbacivanje 'Bearer' iz tokena
        String username = tokenUtils.getUsernameFromToken(cleanToken); //izvlacenje username-a iz tokena
        User user = userService.findByUsername(username); //provera da li postoji u bazi

        if (user == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        User findUser = userService.findByUsername(queryUsername);

        if (findUser == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        UserDTO userDTO = new UserDTO(findUser);

        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDTO> createUser(@RequestBody @Validated UserDTO newUser) {
        User createdUser = userService.createUser(newUser);

        if (createdUser == null)
            return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);

        UserDTO userDTO = new UserDTO(createdUser);

        return new ResponseEntity<>(userDTO, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<UserTokenState> createAuthenticationToken(
            @RequestBody JwtAuthenticationRequest authenticationRequest) {
        //ukoliko username i password nisu tacni, logovanje nece biti uspesno,bacice exception
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authenticationRequest.getUsername(), authenticationRequest.getPassword()));

        //ubacivanje korisnika u security context ako je prosla autentifikacija
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //kreiranje tokena
        UserDetails user = (UserDetails) authentication.getPrincipal();
        String jwt = tokenUtils.generateToken(user);
        long expiresIn = tokenUtils.getExpiredIn();

        //postavljanje poslednjeg pristupa aplikaciji
        User loggedInUser = userService.findByUsername(authenticationRequest.getUsername());
        loggedInUser.setLastLogin(LocalDateTime.now());
        userService.updateUser(loggedInUser);

        //vracanje tokena kao odgovor na uspesnu autentifikaciju
        return ResponseEntity.ok(new UserTokenState(jwt, expiresIn));
    }

    @GetMapping("/logout")
    public ResponseEntity logoutUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            SecurityContextHolder.clearContext();
            return new ResponseEntity("You have successfully logged out!", HttpStatus.OK);
        }
        return new ResponseEntity("User is not authenticated!", HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/change-password")
    public ResponseEntity<UserDTO> changePassword(
            @RequestBody @Validated ChangePasswordRequest changePasswordRequest,
            @RequestHeader("authorization") String token) {

        String cleanToken = token.substring(7); //izbacivanje 'Bearer' iz tokena
        String username = tokenUtils.getUsernameFromToken(cleanToken); //izvlacenje username-a iz tokena
        User user = userService.findByUsername(username); //provera da li postoji u bazi


        if (user == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        // provera hash-a lozinki u bazi i u zahtevu
        String oldPassRequest = changePasswordRequest.getOldPassword();
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        if (!passwordEncoder.matches(oldPassRequest, user.getPassword()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        user = userService.updateUser(user);

        return new ResponseEntity<>(new UserDTO(user), HttpStatus.OK);
    }

    @PostMapping("/search")
    public ResponseEntity<List<UserDTO>> searchUsers(@RequestBody UserQuery userQuery,
                                                     @RequestHeader("authorization") String token) {
        String cleanToken = token.substring(7); //izbacivanje 'Bearer' iz tokena
        String username = tokenUtils.getUsernameFromToken(cleanToken); //izvlacenje username-a iz tokena
        User user = userService.findByUsername(username); //provera da li postoji u bazi


        if (user == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        List<User> users = userService.searchUsers(userQuery.getName1(), userQuery.getName2());
        List<UserDTO> userDTOS = new ArrayList<>();

        for (User temp: users) {
            userDTOS.add(new UserDTO(temp));
        }

        return new ResponseEntity<>(userDTOS, HttpStatus.OK);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> loadAllUsers() {
        return this.userService.findAll();
    }
}
