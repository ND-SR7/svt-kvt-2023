package com.ftn.ac.rs.svtkvt2023.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//dto za promenu lozinke
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {

    private String oldPassword;
    private String newPassword;
}
