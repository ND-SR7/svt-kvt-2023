package com.ftn.ac.rs.svtkvt2023.service;

import com.ftn.ac.rs.svtkvt2023.model.dto.UserDTO;
import com.ftn.ac.rs.svtkvt2023.model.entity.User;

import java.util.List;

public interface UserService {

    User findById(Long id);

    User findByUsername(String username);

    List<User> findAll();

    User createUser(UserDTO userDTO);

    User updateUser(User user);

    Long deleteUser(Long id);
}
