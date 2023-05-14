package com.ftn.ac.rs.svtkvt2023.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//dto koji enkapsulira generisani JWT token
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserTokenState {

    private String accessToken;
    private Long expiresIn;
}
