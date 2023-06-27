package com.ftn.ac.rs.svtkvt2023.controller;

import com.ftn.ac.rs.svtkvt2023.model.dto.*;
import com.ftn.ac.rs.svtkvt2023.model.entity.Image;
import com.ftn.ac.rs.svtkvt2023.model.entity.User;
import com.ftn.ac.rs.svtkvt2023.security.TokenUtils;
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
import java.util.List;

@RestController
@RequestMapping("api/users")
public class UserController {

    UserService userService;

    UserDetailsService userDetailsService;

    ImageService imageService;

    AuthenticationManager authenticationManager;

    TokenUtils tokenUtils;

    @Autowired
    public UserController(UserServiceImpl userService, AuthenticationManager authenticationManager,
                          UserDetailsService userDetailsService, ImageService imageService, TokenUtils tokenUtils){
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.imageService = imageService;
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

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> loadAllUsers() {
        return this.userService.findAll();
    }
}
