package com.negeso.crypto.data.dto;

import lombok.Data;

@Data
public class RegistrationDto {
    private String email;
    private String password;
    private String repeatPassword;
    private String profilePictureUrl;
}
