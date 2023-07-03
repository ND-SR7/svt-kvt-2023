package com.ftn.ac.rs.svtkvt2023.controller;

import com.ftn.ac.rs.svtkvt2023.model.dto.*;
import com.ftn.ac.rs.svtkvt2023.model.entity.*;
import com.ftn.ac.rs.svtkvt2023.security.TokenUtils;
import com.ftn.ac.rs.svtkvt2023.service.FriendRequestService;
import com.ftn.ac.rs.svtkvt2023.service.GroupService;
import com.ftn.ac.rs.svtkvt2023.service.ImageService;
import com.ftn.ac.rs.svtkvt2023.service.UserService;
import com.ftn.ac.rs.svtkvt2023.service.impl.UserServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private static final Logger logger = LogManager.getLogger(UserController.class);

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
        logger.info("Checking authorization");
        String cleanToken = token.substring(7); //izbacivanje 'Bearer' iz tokena
        String username = tokenUtils.getUsernameFromToken(cleanToken); //izvlacenje username-a iz tokena
        User user = userService.findByUsername(username); //provera da li postoji u bazi

        if (user == null) {
            logger.error("User not found for token: " + cleanToken);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        logger.info("Finding user for id: " + id);
        User findUser = userService.findById(Long.parseLong(id));

        if (findUser == null) {
            logger.error("User not found for id: " + id);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        logger.info("Creating response");
        UserDTO userDTO = new UserDTO(findUser);
        logger.info("Created and sent response");

        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @PatchMapping("/edit")
    public ResponseEntity<UserDTO> editUser(@RequestBody @Validated UserDTO editedUser,
                                            @RequestHeader("authorization") String token) {
        logger.info("Checking authorization");
        String cleanToken = token.substring(7); //izbacivanje 'Bearer' iz tokena
        String username = tokenUtils.getUsernameFromToken(cleanToken); //izvlacenje username-a iz tokena
        User user = userService.findByUsername(username); //provera da li postoji u bazi

        if (user == null) {
            logger.error("User not found for token: " + cleanToken);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        logger.info("Finding original user for id: " + editedUser.getId());
        User oldUser = userService.findById(editedUser.getId());

        if (oldUser == null) {
            logger.error("Original user not found for id: " + editedUser.getId());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        logger.info("Applying changes");
        if (editedUser.getDisplayName() != null)
            oldUser.setDisplayName(editedUser.getDisplayName());

        if (editedUser.getDescription() != null)
            oldUser.setDescription(editedUser.getDescription());

        Image oldImage = imageService.findProfileImageForUser(user.getId());

        if (editedUser.getProfileImage() != null && oldImage == null)
            imageService.createImage(editedUser.getProfileImage());
        else if (editedUser.getProfileImage() != null){
            oldImage.setPath(editedUser.getProfileImage().getPath());
            imageService.updateImage(oldImage);
        }

        oldUser = userService.updateUser(oldUser);

        logger.info("Creating response");
        UserDTO updatedUser = new UserDTO(oldUser);
        logger.info("Created and sent response");

        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<ImageDTO> getProfileImage(@PathVariable String id,
                                           @RequestHeader("authorization") String token) {
        logger.info("Checking authorization");
        String cleanToken = token.substring(7); //izbacivanje 'Bearer' iz tokena
        String username = tokenUtils.getUsernameFromToken(cleanToken); //izvlacenje username-a iz tokena
        User user = userService.findByUsername(username); //provera da li postoji u bazi

        if (user == null) {
            logger.error("User not found for token: " + cleanToken);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        logger.info("Finding profile image for user with id: " + id);
        Image image = imageService.findProfileImageForUser(Long.parseLong(id));

        if (image == null) {
            logger.info("Image not found for user with id: " + id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        logger.info("Creating response");
        ImageDTO imageDTO = new ImageDTO(image);
        logger.info("Created and sent response");

        return new ResponseEntity<>(imageDTO, HttpStatus.OK);
    }

    @GetMapping("/{id}/groups")
    public ResponseEntity<List<GroupDTO>> getUserGroups(@PathVariable String id,
                                                    @RequestHeader("authorization") String token) {
        logger.info("Checking authorization");
        String cleanToken = token.substring(7); //izbacivanje 'Bearer' iz tokena
        String username = tokenUtils.getUsernameFromToken(cleanToken); //izvlacenje username-a iz tokena
        User user = userService.findByUsername(username); //provera da li postoji u bazi

        if (user == null) {
            logger.error("User not found for token: " + cleanToken);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        logger.info("Finding groups user with id: " + id + " is member of");
        List<Group> groups = groupService.findGroupsForUser(Long.parseLong(id));
        List<GroupDTO> groupDTOS = new ArrayList<>();

        logger.info("Creating response");
        for (Group group: groups) {
            groupDTOS.add(new GroupDTO(group));
        }
        logger.info("Created and sent response");

        return new ResponseEntity<>(groupDTOS, HttpStatus.OK);
    }

    @GetMapping("group/{id}/admins")
    public ResponseEntity<List<UserDTO>> getGroupAdmins(@PathVariable String id,
                                                        @RequestHeader("authorization") String token) {
        logger.info("Checking authorization");
        String cleanToken = token.substring(7); //izbacivanje 'Bearer' iz tokena
        String username = tokenUtils.getUsernameFromToken(cleanToken); //izvlacenje username-a iz tokena
        User user = userService.findByUsername(username); //provera da li postoji u bazi

        if (user == null) {
            logger.error("User not found for token: " + cleanToken);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        logger.info("Finding admins for group with id: " + id);
        List<User> users = userService.findGroupAdmins(Long.parseLong(id));
        List<UserDTO> userDTOS = new ArrayList<>();

        logger.info("Creating response");
        for (User temp: users) {
            userDTOS.add(new UserDTO(temp));
        }
        logger.info("Created and sent response");

        return new ResponseEntity<>(userDTOS, HttpStatus.OK);
    }

    @GetMapping("/friend-request")
    public ResponseEntity<List<FriendRequestDTO>> getFriendRequests(@RequestHeader("authorization") String token) {
        logger.info("Checking authorization");
        String cleanToken = token.substring(7); //izbacivanje 'Bearer' iz tokena
        String username = tokenUtils.getUsernameFromToken(cleanToken); //izvlacenje username-a iz tokena
        User user = userService.findByUsername(username); //provera da li postoji u bazi

        if (user == null) {
            logger.error("User not found for token: " + cleanToken);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        logger.info("Finding friend requests for user with id: " + user.getId());
        List<FriendRequest> friendRequestsFromUser = friendRequestService.findAllFromUser(user.getId());
        List<FriendRequest> friendRequestsToUser = friendRequestService.findAllToUser(user.getId());
        List<FriendRequestDTO> friendRequestDTOS = new ArrayList<>();

        logger.info("Creating response");
        for (FriendRequest friendRequest: friendRequestsFromUser)
            friendRequestDTOS.add(new FriendRequestDTO(friendRequest));

        for (FriendRequest friendRequest: friendRequestsToUser)
            friendRequestDTOS.add(new FriendRequestDTO(friendRequest));
        logger.info("Created and sent response");

        return new ResponseEntity<>(friendRequestDTOS, HttpStatus.OK);
    }

    @PatchMapping("/friend-request")
    public ResponseEntity<FriendRequestDTO> updateFriendRequest(@RequestBody @Validated FriendRequestDTO friendRequestDTO,
                                                                @RequestHeader("authorization") String token) {
        logger.info("Checking authorization");
        String cleanToken = token.substring(7); //izbacivanje 'Bearer' iz tokena
        String username = tokenUtils.getUsernameFromToken(cleanToken); //izvlacenje username-a iz tokena
        User user = userService.findByUsername(username); //provera da li postoji u bazi

        if (user == null) {
            logger.error("User not found for token: " + cleanToken);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        logger.info("Finding original friend request for id: " + friendRequestDTO.getId());
        FriendRequest oldFriendRequest = friendRequestService.findById(friendRequestDTO.getId());
        if (oldFriendRequest == null) {
            logger.error("Original friend request not found for id: " + friendRequestDTO.getId());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        logger.info("Applying changes");
        if (friendRequestDTO.isApproved())
            userService.addFriendship(friendRequestDTO.getFromUserId(), friendRequestDTO.getToUserId());

        oldFriendRequest.setApproved(friendRequestDTO.isApproved());
        oldFriendRequest.setAt(LocalDateTime.parse(friendRequestDTO.getAt()));

        oldFriendRequest = friendRequestService.updateFriendRequest(oldFriendRequest);

        logger.info("Creating response");
        FriendRequestDTO newFriendRequest = new FriendRequestDTO(oldFriendRequest);
        logger.info("Created and sent response");

        return new ResponseEntity<>(newFriendRequest, HttpStatus.OK);
    }

    @DeleteMapping("/friend-request/{id}")
    public ResponseEntity deleteFriendRequest(@PathVariable String id,
                                              @RequestHeader("authorization") String token) {
        logger.info("Checking authorization");
        String cleanToken = token.substring(7); //izbacivanje 'Bearer' iz tokena
        String username = tokenUtils.getUsernameFromToken(cleanToken); //izvlacenje username-a iz tokena
        User user = userService.findByUsername(username); //provera da li postoji u bazi

        if (user == null) {
            logger.error("User not found for token: " + cleanToken);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        logger.info("Deleting friend request with id: " + id);
        Integer deleted = friendRequestService.deleteFriendRequest(Long.parseLong(id));

        if (deleted != 0) {
            logger.info("Successfully deleted friend request with id: " + id);
            return new ResponseEntity(deleted, HttpStatus.NO_CONTENT);
        }
        logger.error("Failed to delete friend request with id: " + id);
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/{id}/friend-request")
    public ResponseEntity<Boolean> saveFriendRequest(@PathVariable String id,
                                                     @RequestBody @Validated FriendRequestDTO friendRequestDTO,
                                                     @RequestHeader("authorization") String token) {
        logger.info("Checking authorization");
        String cleanToken = token.substring(7); //izbacivanje 'Bearer' iz tokena
        String username = tokenUtils.getUsernameFromToken(cleanToken); //izvlacenje username-a iz tokena
        User user = userService.findByUsername(username); //provera da li postoji u bazi

        if (user == null) {
            logger.error("User not found for token: " + cleanToken);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        logger.info("Creating friend request from DTO");
        FriendRequest friendRequest = friendRequestService.createFriendRequest(friendRequestDTO);

        if (friendRequest == null) {
            logger.error("Friend request couldn't be created from DTO");
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }
        logger.info("Successfully saved friend request for user with id: " + id);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @GetMapping("/friends")
    public ResponseEntity<List<UserDTO>> getUserFriends(@RequestHeader("authorization") String token) {
        logger.info("Checking authorization");
        String cleanToken = token.substring(7); //izbacivanje 'Bearer' iz tokena
        String username = tokenUtils.getUsernameFromToken(cleanToken); //izvlacenje username-a iz tokena
        User user = userService.findByUsername(username); //provera da li postoji u bazi

        if (user == null) {
            logger.error("User not found for token: " + cleanToken);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        logger.info("Finding friends of user with id: " + user.getId());
        List<User> users = userService.findFriendsForUser(user.getId());
        List<UserDTO> userDTOS = new ArrayList<>();

        logger.info("Creating response");
        for (User temp: users) {
            userDTOS.add(new UserDTO(temp));
        }
        logger.info("Created and sent response");

        return new ResponseEntity<>(userDTOS, HttpStatus.OK);
    }

    @GetMapping("/user/{queryUsername}")
    public ResponseEntity<UserDTO> getOneByUsername(@PathVariable String queryUsername,
                                          @RequestHeader("authorization") String token) {
        logger.info("Checking authorization");
        String cleanToken = token.substring(7); //izbacivanje 'Bearer' iz tokena
        String username = tokenUtils.getUsernameFromToken(cleanToken); //izvlacenje username-a iz tokena
        User user = userService.findByUsername(username); //provera da li postoji u bazi

        if (user == null) {
            logger.error("User not found for token: " + cleanToken);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        logger.info("Finding user for username: " + queryUsername);
        User findUser = userService.findByUsername(queryUsername);

        if (findUser == null) {
            logger.error("User not found for username: " + queryUsername);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        logger.info("Creating response");
        UserDTO userDTO = new UserDTO(findUser);
        logger.info("Created and sent response");

        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDTO> createUser(@RequestBody @Validated UserDTO newUser) {
        logger.info("Creating user from DTO");
        User createdUser = userService.createUser(newUser);

        if (createdUser == null) {
            logger.error("User couldn't be created from DTO");
            return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
        }

        logger.info("Creating response");
        UserDTO userDTO = new UserDTO(createdUser);
        logger.info("Created and sent response");

        return new ResponseEntity<>(userDTO, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<UserTokenState> createAuthenticationToken(
            @RequestBody JwtAuthenticationRequest authenticationRequest) {
        //ukoliko username i password nisu tacni, logovanje nece biti uspesno,bacice exception
        logger.info("Checking user's username and password");
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authenticationRequest.getUsername(), authenticationRequest.getPassword()));

        //ubacivanje korisnika u security context ako je prosla autentifikacija
        logger.info("Putting user in security context");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //kreiranje tokena
        logger.info("Creating token for user");
        UserDetails user = (UserDetails) authentication.getPrincipal();
        String jwt = tokenUtils.generateToken(user);
        long expiresIn = tokenUtils.getExpiredIn();

        //postavljanje poslednjeg pristupa aplikaciji
        logger.info("Setting last login time for user");
        User loggedInUser = userService.findByUsername(authenticationRequest.getUsername());
        loggedInUser.setLastLogin(LocalDateTime.now());
        userService.updateUser(loggedInUser);

        //vracanje tokena kao odgovor na uspesnu autentifikaciju
        logger.info("Created and sent token");
        return ResponseEntity.ok(new UserTokenState(jwt, expiresIn));
    }

    @GetMapping("/logout")
    public ResponseEntity logoutUser() {
        logger.info("Getting authentication from security context");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        logger.info("Checking if authentication is anonymous");
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            SecurityContextHolder.clearContext();
            logger.info("User successfully logged out");
            return new ResponseEntity("You have successfully logged out!", HttpStatus.OK);
        }
        logger.error("User is not authenticated and can't be logged out");
        return new ResponseEntity("User is not authenticated!", HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/change-password")
    public ResponseEntity<UserDTO> changePassword(
            @RequestBody @Validated ChangePasswordRequest changePasswordRequest,
            @RequestHeader("authorization") String token) {

        logger.info("Checking authorization");
        String cleanToken = token.substring(7); //izbacivanje 'Bearer' iz tokena
        String username = tokenUtils.getUsernameFromToken(cleanToken); //izvlacenje username-a iz tokena
        User user = userService.findByUsername(username); //provera da li postoji u bazi

        if (user == null) {
            logger.error("User not found for token: " + cleanToken);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // provera hash-a lozinki u bazi i u zahtevu
        logger.info("Comparing hashes in request and database");
        String oldPassRequest = changePasswordRequest.getOldPassword();
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        if (!passwordEncoder.matches(oldPassRequest, user.getPassword())) {
            logger.error("Hashes do not match");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        logger.info("Changing password for user with id: " + user.getId());
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        user = userService.updateUser(user);

        logger.info("Password changed");
        return new ResponseEntity<>(new UserDTO(user), HttpStatus.OK);
    }

    @PostMapping("/search")
    public ResponseEntity<List<UserDTO>> searchUsers(@RequestBody UserQuery userQuery,
                                                     @RequestHeader("authorization") String token) {
        logger.info("Checking authorization");
        String cleanToken = token.substring(7); //izbacivanje 'Bearer' iz tokena
        String username = tokenUtils.getUsernameFromToken(cleanToken); //izvlacenje username-a iz tokena
        User user = userService.findByUsername(username); //provera da li postoji u bazi

        if (user == null) {
            logger.error("User not found for token: " + cleanToken);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        logger.info("Finding users that match query");
        List<User> users = userService.searchUsers(userQuery.getName1(), userQuery.getName2());
        List<UserDTO> userDTOS = new ArrayList<>();

        logger.info("Creating response");
        for (User temp: users) {
            userDTOS.add(new UserDTO(temp));
        }
        logger.info("Created and sent response");

        return new ResponseEntity<>(userDTOS, HttpStatus.OK);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> loadAllUsers() {
        logger.info("Returning all users per system admin request");
        return this.userService.findAll();
    }
}
