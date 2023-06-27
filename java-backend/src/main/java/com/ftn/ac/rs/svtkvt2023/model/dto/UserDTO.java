package com.ftn.ac.rs.svtkvt2023.model.dto;

import com.ftn.ac.rs.svtkvt2023.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    private String lastLogin;

    @NotBlank
    private String email;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String displayName;

    private String description;

    public UserDTO(User createdUser) {
        this.id = createdUser.getId();
        this.username = createdUser.getUsername();
        this.email = createdUser.getEmail();
        if (createdUser.getLastLogin() != null)
            this.lastLogin = createdUser.getLastLogin().toString();
        this.firstName = createdUser.getFirstName();
        this.lastName = createdUser.getLastName();
        this.displayName = createdUser.getDisplayName();
        this.description = createdUser.getDescription();
    }
}
