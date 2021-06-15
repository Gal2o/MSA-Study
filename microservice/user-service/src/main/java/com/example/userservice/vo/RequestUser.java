package com.example.userservice.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RequestUser {
    @NotNull(message = "Email cannot be null")
    @Size(min = 2, message = "Email not be less than 2 char")
    private String email;

    @NotNull(message = "Name cannot be null")
    @Size(min = 2, message = "Name not be less than 2 char")
    private String name;

    @NotNull(message = "Password cannot be null")
    @Size(min = 8, message = "Password not be less than 8 char")
    private String pwd;
}
