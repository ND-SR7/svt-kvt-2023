package com.ftn.ac.rs.svtkvt2023.service.impl;

import com.ftn.ac.rs.svtkvt2023.model.dto.UserDTO;
import com.ftn.ac.rs.svtkvt2023.model.entity.User;
import com.ftn.ac.rs.svtkvt2023.repository.UserRepository;
import com.ftn.ac.rs.svtkvt2023.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User findById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (!user.isEmpty())
            return user.get();
        return null;
    }

    @Override
    public User findByUsername(String username) {
        Optional<User> user = userRepository.findFirstByUsername(username);
        if (!user.isEmpty())
            return user.get();
        return null;
    }

    @Override
    public List<User> findAll() {
        return this.userRepository.findAll();
    }

    @Override
    public List<User> findFriendsForUser(Long userId) {
        Optional<List<User>> users = userRepository.findFriendsByUserId(userId);
        if (!users.isEmpty())
            return users.get();
        return null;
    }

    @Override
    public Integer addFriendship(Long userId, Long friendId) {
        return userRepository.saveFriendship(userId, friendId);
    }

    @Override
    public List<User> searchUsers(String name1, String name2) {
        Optional<List<User>> users = userRepository.findUsersByQuery(name1, name2);
        if (!users.isEmpty())
            return users.get();
        return null;
    }

    @Override
    public User createUser(UserDTO userDTO) {
        Optional<User> user = userRepository.findFirstByUsername(userDTO.getUsername());

        if(user.isPresent())
            return null;

        User newUser = new User();
        newUser.setUsername(userDTO.getUsername());
        newUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        newUser.setEmail(userDTO.getEmail());
        newUser.setFirstName(userDTO.getFirstName());
        newUser.setLastName(userDTO.getLastName());
        newUser.setAdmin(false);
        newUser.setDeleted(false);
        newUser = userRepository.save(newUser);

        return newUser;
    }

    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public Integer deleteUser(Long id) {
        return userRepository.deleteUserById(id);
    }
}
