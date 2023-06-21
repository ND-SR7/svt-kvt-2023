package com.ftn.ac.rs.svtkvt2023.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.validation.constraints.NotBlank;

//dto za login
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthenticationRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
